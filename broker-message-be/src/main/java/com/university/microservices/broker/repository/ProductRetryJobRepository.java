package com.university.microservices.broker.repository;

import com.university.microservices.broker.entity.ProductRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRetryJobRepository extends JpaRepository<ProductRetryJob, UUID> {
    
    @Query("SELECT j FROM ProductRetryJob j WHERE j.status = :status AND j.nextRunAt <= CURRENT_TIMESTAMP")
    List<ProductRetryJob> findPendingJobs(String status);
}
