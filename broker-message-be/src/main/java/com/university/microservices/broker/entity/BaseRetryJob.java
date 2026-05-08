package com.university.microservices.broker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseRetryJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String requestData;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String responseData;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private Integer attempt = 1;

    @Column(nullable = false)
    private String status = "SCHEDULED";

    @Column(nullable = false)
    private LocalDateTime nextRunAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getRequestData() { return requestData; }
    public void setRequestData(String requestData) { this.requestData = requestData; }
    
    public String getResponseData() { return responseData; }
    public void setResponseData(String responseData) { this.responseData = responseData; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public Integer getAttempt() { return attempt; }
    public void setAttempt(Integer attempt) { this.attempt = attempt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getNextRunAt() { return nextRunAt; }
    public void setNextRunAt(LocalDateTime nextRunAt) { this.nextRunAt = nextRunAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public abstract String getReferenceId();
    public abstract void setReferenceId(String referenceId);
}
