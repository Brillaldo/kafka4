package com.university.microservices.broker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/simulate")
public class SimulationController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public SimulationController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/failed-product")
    public ResponseEntity<String> simulateFailedProduct(@RequestBody Map<String, Object> payload) {
        // Enviar el payload genérico convertido a string simulando JSON hacia el tópico de fallo
        try {
            String jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send("product_retry_jobs", jsonPayload);
            return ResponseEntity.ok("MocK de fallo de producto inyectado correctamente a Kafka: " + jsonPayload);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/failed-payment")
    public ResponseEntity<String> simulateFailedPayment(@RequestBody Map<String, Object> payload) {
        try {
            String jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send("payments_retry_jobs", jsonPayload);
            return ResponseEntity.ok("MocK de fallo de pago inyectado correctamente a Kafka: " + jsonPayload);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/failed-order")
    public ResponseEntity<String> simulateFailedOrder(@RequestBody Map<String, Object> payload) {
        try {
            String jsonPayload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload);
            kafkaTemplate.send("order_retry_jobs", jsonPayload);
            return ResponseEntity.ok("MocK de fallo de orden inyectado correctamente a Kafka: " + jsonPayload);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}
