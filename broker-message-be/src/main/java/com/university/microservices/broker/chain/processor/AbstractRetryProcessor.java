package com.university.microservices.broker.chain.processor;

import com.university.microservices.broker.chain.RetryContext;
import com.university.microservices.broker.chain.RetryStep;
import com.university.microservices.broker.chain.step.PasoARetryOperationStep;
import com.university.microservices.broker.chain.step.PasoBSendEmailStep;
import com.university.microservices.broker.chain.step.PasoCUpdateDbStep;
import com.university.microservices.broker.chain.step.PasoDCreateMongoRecordStep;
import com.university.microservices.broker.entity.BaseRetryJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRetryProcessor<T extends BaseRetryJob> {

    private final PasoARetryOperationStep pasoA;
    private final PasoBSendEmailStep pasoB;
    private final PasoCUpdateDbStep pasoC;
    private final PasoDCreateMongoRecordStep pasoD;

    private RetryStep chain;

    @PostConstruct
    public void initChain() {
        pasoA.setNext(pasoB);
        pasoB.setNext(pasoC);
        pasoC.setNext(pasoD);
        chain = pasoA;
    }

    protected abstract JpaRepository<T, java.util.UUID> getRepository();
    protected abstract String getEntityType();

    public void processJob(T job) {
        log.info("Processing job {} for entity {}", job.getId(), getEntityType());
        
        job.setAttempt(job.getAttempt() + 1);

        RetryContext context = new RetryContext(
            job.getId(), 
            getEntityType(), 
            job.getReferenceId(), 
            job.getAction(), 
            job.getRequestData(), 
            job.getResponseData()
        );

        try {
            chain.execute(context);
        } catch (Exception e) {
            log.error("Error processing job chain for {} id {}: {}", getEntityType(), job.getId(), e.getMessage());
            context.setStatus("FAILED");
            context.updateStepStatus("processError", "FAILED", e.getMessage());
        } finally {
            job.setStatus(context.getStatus());
            job.setResponseData(context.getResponseDataAsString());
            job.setUpdatedAt(LocalDateTime.now());
            getRepository().save(job);
        }
    }
}
