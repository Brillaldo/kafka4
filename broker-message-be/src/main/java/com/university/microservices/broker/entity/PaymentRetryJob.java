package com.university.microservices.broker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments_retry_jobs")
public class PaymentRetryJob extends BaseRetryJob {

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Override
    public String getReferenceId() {
        return paymentId;
    }

    @Override
    public void setReferenceId(String referenceId) {
        this.paymentId = referenceId;
    }
}
