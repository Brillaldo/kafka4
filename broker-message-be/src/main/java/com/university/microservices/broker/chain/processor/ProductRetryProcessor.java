package com.university.microservices.broker.chain.processor;

import com.university.microservices.broker.chain.step.PasoARetryOperationStep;
import com.university.microservices.broker.chain.step.PasoBSendEmailStep;
import com.university.microservices.broker.chain.step.PasoCUpdateDbStep;
import com.university.microservices.broker.chain.step.PasoDCreateMongoRecordStep;
import com.university.microservices.broker.entity.ProductRetryJob;
import com.university.microservices.broker.repository.ProductRetryJobRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductRetryProcessor extends AbstractRetryProcessor<ProductRetryJob> {

    private final ProductRetryJobRepository repository;

    public ProductRetryProcessor(PasoARetryOperationStep pasoA,
                                 PasoBSendEmailStep pasoB,
                                 PasoCUpdateDbStep pasoC,
                                 PasoDCreateMongoRecordStep pasoD,
                                 ProductRetryJobRepository repository) {
        super(pasoA, pasoB, pasoC, pasoD);
        this.repository = repository;
    }

    @Override
    protected JpaRepository<ProductRetryJob, UUID> getRepository() {
        return repository;
    }

    @Override
    protected String getEntityType() {
        return "PRODUCT";
    }
}
