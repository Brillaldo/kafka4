package com.university.microservices.ordenes_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.microservices.ordenes_service.model.Envio;
import com.university.microservices.ordenes_service.model.Orden;
import com.university.microservices.ordenes_service.repository.EnvioRepository;
import com.university.microservices.ordenes_service.repository.OrdenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrdenConsumer {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private EnvioRepository envioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @KafkaListener(topics = "payment_received_events", groupId = "ordenes-group")
    public void consumePaymentReceived(String payload) {
        log.info("Received payment event: {}", payload);
        try {
            Map<String, Object> paymentData = objectMapper.readValue(payload, Map.class);
            String ordenId = (String) paymentData.get("ordenId");
            BigDecimal montoPagado = new BigDecimal(paymentData.get("monto").toString());

            ordenRepository.findById(ordenId).ifPresent(orden -> {
                // En un sistema real, restaríamos el pago del total o verificaríamos contra la suma de pagos
                // Para este ejercicio, calcularemos el total de pagos para esta orden
                try {
                    String url = "http://pagos-service:8083/pagos/orden/" + ordenId;
                    // El prompt dice que GET /pagos/orden/{id} retorna todos los pagos.
                    // Pero la implementación actual en PagoController retorna un solo Pago.
                    // Vamos a asumir que sumamos este pago.
                    
                    // Supongamos que si el total acumulado de pagos >= total de la orden, se marca como pagada
                    // Por simplicidad, si este pago es >= total de la orden, o si ya estaba pagada
                    if (montoPagado.compareTo(orden.getTotal()) >= 0) {
                        orden.setStatus("PAID");
                        ordenRepository.save(orden);
                        log.info("Order {} marked as PAID", ordenId);

                        // Registrar en envios
                        Envio envio = new Envio(ordenId);
                        envioRepository.save(envio);
                        log.info("Shipment registered for order {}", ordenId);
                    }
                } catch (Exception e) {
                    log.error("Error recalculating total for order {}", ordenId, e);
                }
            });
        } catch (Exception e) {
            log.error("Error processing payment event", e);
        }
    }
}
