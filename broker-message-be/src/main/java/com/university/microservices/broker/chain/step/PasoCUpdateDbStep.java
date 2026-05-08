package com.university.microservices.broker.chain.step;

import com.university.microservices.broker.chain.RetryContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PasoCUpdateDbStep extends AbstractRetryStep {

    @Override
    protected void process(RetryContext context) throws Exception {
        log.info("PASO C: Updating state to SUCCESS for entity: {} with id: {}", 
                 context.getEntityType(), context.getJobId());
        
        context.setStatus("SUCCESS");
        context.updateStepStatus("updateRetryJobs", "SUCCESS", "Status ready to be updated in DB");
        log.info("PASO C: Success");
    }
}
