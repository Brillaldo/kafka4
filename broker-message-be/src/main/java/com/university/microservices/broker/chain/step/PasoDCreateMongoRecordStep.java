package com.university.microservices.broker.chain.step;

import com.university.microservices.broker.chain.RetryContext;
import com.university.microservices.broker.mongo.MongoRetryHistory;
import com.university.microservices.broker.mongo.MongoRetryHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasoDCreateMongoRecordStep extends AbstractRetryStep {

    private final MongoRetryHistoryRepository mongoRepository;

    @Override
    protected void process(RetryContext context) throws Exception {
        log.info("PASO D: Creating final record in MongoDB for entity: {} with id: {}", 
                 context.getEntityType(), context.getJobId());
        
        try {
            MongoRetryHistory history = new MongoRetryHistory();
            history.setPostgresJobId(context.getJobId());
            history.setEntityType(context.getEntityType());
            history.setReferenceId(context.getReferenceId());
            history.setAction(context.getAction());
            history.setRequestData(context.getRequestData());
            history.setResponseData(context.getResponseData());
            history.setFinalStatus(context.getStatus());
            history.setExecutionTime(LocalDateTime.now());
            
            mongoRepository.save(history);

            context.updateStepStatus("createMongoRecord", "SUCCESS", "Saved safely to MongoDB");
            log.info("PASO D: Success");
        } catch (Exception e) {
            log.error("PASO D: Error saving to MongoDB", e);
            throw new RuntimeException("Error saving to MongoDB: " + e.getMessage());
        }
    }
}
