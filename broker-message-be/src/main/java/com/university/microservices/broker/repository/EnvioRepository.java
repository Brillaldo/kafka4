package com.university.microservices.broker.repository;

import com.university.microservices.broker.entity.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    List<Envio> findByStatus(String status);
}
