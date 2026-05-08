package com.university.microservices.broker.consumer;

import com.university.microservices.broker.eventchain.EventContext;
import com.university.microservices.broker.eventchain.step.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumer {

    private final SendEmailEventStep sendEmailStep;
    private final OrderStatusValidationStep orderStatusValidationStep;
    private final PaymentCompletionValidationStep paymentCompletionValidationStep;
    private final RegisterEnvioStep registerEnvioStep;
    private final InventoryUpdateEventStep inventoryUpdateEventStep;

    @KafkaListener(topics = "order_status_changed_events", groupId = "broker-message-group")
    public void consumeOrderStatusChanged(String payload) {
        log.info("Received order status changed event: {}", payload);
        try {
            EventContext context = new EventContext("order_status_changed_events", payload);
            
            // Configurar cadena: Email -> Validar Pagado -> Registrar Envío
            sendEmailStep.setNext(orderStatusValidationStep);
            orderStatusValidationStep.setNext(registerEnvioStep);
            registerEnvioStep.setNext(null);
            
            sendEmailStep.execute(context);
        } catch (Exception e) {
            log.error("Error processing order status changed event", e);
        }
    }

    @KafkaListener(topics = "inventory_update_events", groupId = "broker-message-group")
    public void consumeInventoryUpdate(String payload) {
        log.info("Received inventory update event: {}", payload);
        try {
            EventContext context = new EventContext("inventory_update_events", payload);
            
            // Configurar cadena: Actualizar Inventario
            inventoryUpdateEventStep.setNext(null);
            
            inventoryUpdateEventStep.execute(context);
        } catch (Exception e) {
            log.error("Error processing inventory update event", e);
        }
    }

    @KafkaListener(topics = "payment_received_events", groupId = "broker-message-group")
    public void consumePaymentReceived(String payload) {
        log.info("Received payment received event: {}", payload);
        try {
            EventContext context = new EventContext("payment_received_events", payload);
            
            // Configurar cadena: Email -> Validar Pago Completo -> Registrar Envío
            sendEmailStep.setNext(paymentCompletionValidationStep);
            paymentCompletionValidationStep.setNext(registerEnvioStep);
            registerEnvioStep.setNext(null);
            
            sendEmailStep.execute(context);
        } catch (Exception e) {
            log.error("Error processing payment received event", e);
        }
    }
}
