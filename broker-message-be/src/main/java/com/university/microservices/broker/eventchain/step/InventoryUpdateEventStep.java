package com.university.microservices.broker.eventchain.step;

import com.university.microservices.broker.eventchain.AbstractEventStep;
import com.university.microservices.broker.eventchain.EventContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryUpdateEventStep extends AbstractEventStep {

    private final RestTemplate restTemplate;

    @Override
    protected void process(EventContext context) throws Exception {
        log.info("Processing inventory update for order: {}", context.getData().get("id"));
        List<String> productosIds = (List<String>) context.getData().get("productosIds");

        if (productosIds != null) {
            for (String productoId : productosIds) {
                try {
                    String url = "http://productos-service:8081/productos/" + productoId;
                    Map<String, Object> producto = restTemplate.getForObject(url, Map.class);
                    
                    if (producto != null) {
                        Integer currentQuantity = (Integer) producto.get("quantity");
                        if (currentQuantity != null && currentQuantity > 0) {
                            producto.put("quantity", currentQuantity - 1);
                            restTemplate.put(url, producto);
                            log.info("Inventory updated for product: {}", productoId);
                        }
                    }
                } catch (Exception e) {
                    log.error("Error updating inventory for product: {}", productoId, e);
                }
            }
        }
    }
}
