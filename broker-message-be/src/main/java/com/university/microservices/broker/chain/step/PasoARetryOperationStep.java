package com.university.microservices.broker.chain.step;

import com.university.microservices.broker.chain.RetryContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasoARetryOperationStep extends AbstractRetryStep {

    @Override
    protected void process(RetryContext context) throws Exception {
        log.info("PASO A: Executing retry operation for entity: {} with id: {}", 
                 context.getEntityType(), context.getJobId());
        
        try {
            // Simulating HTTP call to external service
            boolean simulateError = true; // SIMULATE ERROR TO SHOW EMAIL FAILURE
            if (simulateError) {
                throw new RuntimeException("External service unavailable");
            }
            context.setStatus("SUCCESS");
            context.updateStepStatus("retryOperation", "SUCCESS", "Call simulated successfully");
            log.info("PASO A: Success");
        } catch (Exception e) {
            context.setStatus("FAILED");
            context.updateStepStatus("retryOperation", "FAILED", e.getMessage());
            log.error("PASO A: Failed");
        }
    }
}
