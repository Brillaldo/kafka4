package com.university.microservices.ordenes_service.controller;

import com.university.microservices.ordenes_service.model.Orden;
import com.university.microservices.ordenes_service.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping
    public ResponseEntity<?> createOrden(@RequestBody Orden orden) {
        try {
            if (orden.getProductosIds() == null || orden.getProductosIds().isEmpty()) {
                throw new RuntimeException("La orden debe contener productos.");
            }
            orden.setStatus("CREATED");
            Orden saved = ordenRepository.save(orden);
            
            // Flujo 2: Emitir evento de actualización de inventario
            try {
                kafkaTemplate.send("inventory_update_events", saved.getId(), new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(saved));
            } catch (Exception e) {
                log.error("Error enviando evento de inventario", e);
            }

            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", orden);
            payload.put("action", "CREATE_ORDER");
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("order_retry_jobs", orden.getId() != null ? orden.getId() : "NEW", new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {}
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error, enviado a reintento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orden> getOrdenById(@PathVariable String id) {
        return ordenRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Orden>> getOrdenesByUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(ordenRepository.findByUsuarioId(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrden(@PathVariable String id, @RequestBody Orden updatedOrden) {
        try {
            return ordenRepository.findById(id).map(existingOrden -> {
                boolean productsChanged = !existingOrden.getProductosIds().equals(updatedOrden.getProductosIds());
                
                existingOrden.setProductosIds(updatedOrden.getProductosIds());
                existingOrden.setTotal(updatedOrden.getTotal());
                existingOrden.setStatus(updatedOrden.getStatus());
                Orden saved = ordenRepository.save(existingOrden);

                // Flujo 2: Si cambian productos, emitir evento
                if (productsChanged) {
                    try {
                        kafkaTemplate.send("inventory_update_events", id, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(saved));
                    } catch (Exception e) {}
                }
                return ResponseEntity.ok(saved);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error actualizando orden: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrdenStatus(@PathVariable String id, @RequestParam String status) {
        try {
            return ordenRepository.findById(id).map(orden -> {
                orden.setStatus(status);
                Orden saved = ordenRepository.save(orden);

                // Flujo 1: Emitir evento de cambio de estatus
                try {
                    kafkaTemplate.send("order_status_changed_events", id, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(saved));
                } catch (Exception e) {}

                return ResponseEntity.ok(saved);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("status", status);
            payload.put("action", "UPDATE_ORDER_STATUS");
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("order_retry_jobs", id, new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {}
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error, enviado a reintento: " + e.getMessage());
        }
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrdenController.class);
}
