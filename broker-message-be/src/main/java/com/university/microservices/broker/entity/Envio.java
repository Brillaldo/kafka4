package com.university.microservices.broker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "envios")
@Getter
@Setter
@NoArgsConstructor
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String ordenId;
    private String status; // PENDING, SENT
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    public Envio(String ordenId) {
        this.ordenId = ordenId;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }
}
