package com.university.microservices.productos_service.controller;

import com.university.microservices.productos_service.model.Producto;
import com.university.microservices.productos_service.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable String id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProducto(@RequestBody Producto producto) {
        try {
            // Simulamos un fallo ocasional si el precio es negativo para probar el
            // broker-message-be
            if (producto.getPrice() != null && producto.getPrice().doubleValue() < 0) {
                throw new RuntimeException("Precio invalido. Simulando fallo para Retry Job.");
            }
            Producto saved = productoRepository.save(producto);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", producto);
            payload.put("action", "CREATE_PRODUCT");
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("product_retry_jobs", producto.getId() != null ? producto.getId() : "NEW",
                        new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {
                // ignore
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando producto, enviado a reintento: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(@PathVariable String id, @RequestBody Producto producto) {
        try {
            if (!productoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            producto.setId(id);
            return ResponseEntity.ok(productoRepository.save(producto));
        } catch (Exception e) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("data", producto);
            payload.put("action", "UPDATE_PRODUCT");
            payload.put("error", e.getMessage());
            try {
                kafkaTemplate.send("product_retry_jobs", id,
                        new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(payload));
            } catch (Exception jsonEx) {
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error procesando producto, enviado a reintento: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
        if (!productoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
