package com.university.microservices.pagos_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
public class EnviosProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/ordenes/envios")
    public ResponseEntity<?> getEnvios() {
        try {
            // Asumimos que ordenes-service está en el puerto 8082 o accesible vía DNS
            String url = "http://ordenes-service:8082/ordenes/envios";
            return ResponseEntity.ok(restTemplate.getForObject(url, List.class));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener envíos: " + e.getMessage());
        }
    }
}
