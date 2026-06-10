package com.university.microservices.pagos_service.repository;

import com.university.microservices.pagos_service.model.Pago;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
    List<Pago> findByOrdenId(String ordenId);
}
