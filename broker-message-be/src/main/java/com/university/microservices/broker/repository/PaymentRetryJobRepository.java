package com.university.microservices.broker.repository;

import com.university.microservices.broker.entity.PaymentRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRetryJobRepository extends JpaRepository<PaymentRetryJob, UUID> {
    
    @Query("SELECT j FROM PaymentRetryJob j WHERE j.status = :status AND j.nextRunAt <= CURRENT_TIMESTAMP")
    List<PaymentRetryJob> findPendingJobs(String status);
}
