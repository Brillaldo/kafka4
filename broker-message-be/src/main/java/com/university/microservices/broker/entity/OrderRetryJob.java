package com.university.microservices.broker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_retry_jobs")
public class OrderRetryJob extends BaseRetryJob {

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Override
    public String getReferenceId() {
        return orderId;
    }

    @Override
    public void setReferenceId(String referenceId) {
        this.orderId = referenceId;
    }
}
