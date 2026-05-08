package com.university.microservices.broker.eventchain.step;

import com.university.microservices.broker.eventchain.AbstractEventStep;
import com.university.microservices.broker.eventchain.EventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletionValidationStep extends AbstractEventStep {

    private final RestTemplate restTemplate;

    @Override
    protected void process(EventContext context) throws Exception {
        String ordenId = (String) context.getData().get("ordenId");
        log.info("Validating total payment for order: {}", ordenId);

        try {
            // Consultamos el servicio de ordenes para ver el estado actual
            String url = "http://ordenes-service:8082/ordenes/" + ordenId;
            Map<String, Object> orden = restTemplate.getForObject(url, Map.class);
            
            String status = (String) orden.get("status");
            if (!"PAGADO".equalsIgnoreCase(status) && !"PAID".equalsIgnoreCase(status)) {
                log.info("Order is not fully paid. Status: {}. Terminating chain.", status);
                context.setContinueChain(false);
            }
        } catch (Exception e) {
            log.error("Error validating payment completion", e);
            context.setContinueChain(false);
        }
    }
}
