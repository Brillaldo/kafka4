package com.university.microservices.ordenes_service.repository;

import com.university.microservices.ordenes_service.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}
