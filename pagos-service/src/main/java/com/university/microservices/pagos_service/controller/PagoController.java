package com.university.microservices.pagos_service.controller;

import com.university.microservices.pagos_service.model.Pago;
import com.university.microservices.pagos_service.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody Pago pago) {
        try {
            if (pago.getMonto() == null || pago.getMonto().doubleValue() <= 0) {
                throw new RuntimeException("Monto invalido para procesar el pago");
            }
            pago.setStatus("PROCESADO");
            Pago saved = pagoRepository.save(pago);

            // Flujo 3: Emitir evento de pago recibido
            try {
                kafkaTemplate.send("payment_received_events", saved.getId(), new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(saved));
            } catch (Exception e) {}

            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", pago);
            payload.put("action", "PROCESS_PAYMENT");
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("payments_retry_jobs", pago.getId() != null ? pago.getId() : "NEW", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {}
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en pago, enviado a retry: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> getPagoById(@PathVariable String id) {
        return pagoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orden/{id}")
    public ResponseEntity<List<Pago>> getPagoByOrdenId(@PathVariable String id) {
        return ResponseEntity.ok(pagoRepository.findByOrdenId(id));
    }

    @PutMapping("/{id}/reembolso")
    public ResponseEntity<?> reembolsoPago(@PathVariable String id) {
        try {
            return pagoRepository.findById(id).map(pago -> {
                pago.setStatus("REEMBOLSADO");
                return ResponseEntity.ok(pagoRepository.save(pago));
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("action", "REFUND_PAYMENT");
            payload.put("error", e.getMessage());
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("payments_retry_jobs", id, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {}
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fallo en reembolso, enviado a retry: " + e.getMessage());
        }
    }
}
