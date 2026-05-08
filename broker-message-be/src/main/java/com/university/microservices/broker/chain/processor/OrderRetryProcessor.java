package com.university.microservices.broker.chain.processor;

import com.university.microservices.broker.chain.step.PasoARetryOperationStep;
import com.university.microservices.broker.chain.step.PasoBSendEmailStep;
import com.university.microservices.broker.chain.step.PasoCUpdateDbStep;
import com.university.microservices.broker.chain.step.PasoDCreateMongoRecordStep;
import com.university.microservices.broker.entity.OrderRetryJob;
import com.university.microservices.broker.repository.OrderRetryJobRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderRetryProcessor extends AbstractRetryProcessor<OrderRetryJob> {

    private final OrderRetryJobRepository repository;

    public OrderRetryProcessor(PasoARetryOperationStep pasoA,
            PasoBSendEmailStep pasoB,
            PasoCUpdateDbStep pasoC,
            PasoDCreateMongoRecordStep pasoD,
            OrderRetryJobRepository repository) {
        super(pasoA, pasoB, pasoC, pasoD);
        this.repository = repository;
    }

    @Override
    protected JpaRepository<OrderRetryJob, UUID> getRepository() {
        return repository;
    }

    @Override
    protected String getEntityType() {
        return "ORDER";
    }
}
