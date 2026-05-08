package com.university.microservices.broker.scheduler;

import com.university.microservices.broker.chain.processor.OrderRetryProcessor;
import com.university.microservices.broker.chain.processor.PaymentRetryProcessor;
import com.university.microservices.broker.chain.processor.ProductRetryProcessor;
import com.university.microservices.broker.entity.OrderRetryJob;
import com.university.microservices.broker.entity.PaymentRetryJob;
import com.university.microservices.broker.entity.ProductRetryJob;
import com.university.microservices.broker.repository.OrderRetryJobRepository;
import com.university.microservices.broker.repository.PaymentRetryJobRepository;
import com.university.microservices.broker.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryJobScheduler {

    private final PaymentRetryJobRepository paymentRepo;
    private final OrderRetryJobRepository orderRepo;
    private final ProductRetryJobRepository productRepo;

    private final PaymentRetryProcessor paymentProcessor;
    private final OrderRetryProcessor orderProcessor;
    private final ProductRetryProcessor productProcessor;

    @Scheduled(fixedDelayString = "${app.scheduler.retry-delay-ms:10000}")
    public void processPendingJobs() {
        log.info("--- Starting scheduled job for testing SCHEDULED retry processes ---");

        List<PaymentRetryJob> pendingPayments = paymentRepo.findPendingJobs("SCHEDULED");
        for (PaymentRetryJob job : pendingPayments) {
            paymentProcessor.processJob(job);
        }

        List<OrderRetryJob> pendingOrders = orderRepo.findPendingJobs("SCHEDULED");
        for (OrderRetryJob job : pendingOrders) {
            orderProcessor.processJob(job);
        }

        List<ProductRetryJob> pendingProducts = productRepo.findPendingJobs("SCHEDULED");
        for (ProductRetryJob job : pendingProducts) {
            productProcessor.processJob(job);
        }

        log.info("--- Completed scheduled job for SCHEDULED retry processes ---");
    }
}
