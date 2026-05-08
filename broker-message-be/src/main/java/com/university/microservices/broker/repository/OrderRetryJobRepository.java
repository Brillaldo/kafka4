package com.university.microservices.broker.repository;

import com.university.microservices.broker.entity.OrderRetryJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRetryJobRepository extends JpaRepository<OrderRetryJob, UUID> {
    
    @Query("SELECT j FROM OrderRetryJob j WHERE j.status = :status AND j.nextRunAt <= CURRENT_TIMESTAMP")
    List<OrderRetryJob> findPendingJobs(String status);
}
