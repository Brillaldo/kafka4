package com.university.microservices.broker.eventchain.step;

import com.university.microservices.broker.eventchain.AbstractEventStep;
import com.university.microservices.broker.eventchain.EventContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderStatusValidationStep extends AbstractEventStep {

    @Override
    protected void process(EventContext context) throws Exception {
        String status = (String) context.getData().get("status");
        log.info("Validating order status: {}", status);
        
        if (!"Pagado".equalsIgnoreCase(status)) {
            log.info("Status is not 'Pagado'. Terminating chain.");
            context.setContinueChain(false);
        }
    }
}
