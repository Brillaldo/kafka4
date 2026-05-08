package com.university.microservices.broker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "products_retry_jobs")
public class ProductRetryJob extends BaseRetryJob {

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Override
    public String getReferenceId() {
        return productId;
    }

    @Override
    public void setReferenceId(String referenceId) {
        this.productId = referenceId;
    }
}
