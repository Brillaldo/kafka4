package com.university.microservices.ordenes_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ordenId;
    private String status;
    private LocalDateTime sentAt;

    public Envio(String ordenId) {
        this.ordenId = ordenId;
        this.status = "PENDING";
        this.sentAt = LocalDateTime.now();
    }
}
