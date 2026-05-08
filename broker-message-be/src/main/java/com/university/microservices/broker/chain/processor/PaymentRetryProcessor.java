package com.university.microservices.broker.chain.processor;

import com.university.microservices.broker.chain.step.PasoARetryOperationStep;
import com.university.microservices.broker.chain.step.PasoBSendEmailStep;
import com.university.microservices.broker.chain.step.PasoCUpdateDbStep;
import com.university.microservices.broker.chain.step.PasoDCreateMongoRecordStep;
import com.university.microservices.broker.entity.PaymentRetryJob;
import com.university.microservices.broker.repository.PaymentRetryJobRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentRetryProcessor extends AbstractRetryProcessor<PaymentRetryJob> {

    private final PaymentRetryJobRepository repository;

    public PaymentRetryProcessor(PasoARetryOperationStep pasoA,
                                 PasoBSendEmailStep pasoB,
                                 PasoCUpdateDbStep pasoC,
                                 PasoDCreateMongoRecordStep pasoD,
                                 PaymentRetryJobRepository repository) {
        super(pasoA, pasoB, pasoC, pasoD);
        this.repository = repository;
    }

    @Override
    protected JpaRepository<PaymentRetryJob, UUID> getRepository() {
        return repository;
    }

    @Override
    protected String getEntityType() {
        return "PAYMENT";
    }
}
