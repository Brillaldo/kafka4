package com.university.microservices.broker.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.microservices.broker.entity.OrderRetryJob;
import com.university.microservices.broker.entity.PaymentRetryJob;
import com.university.microservices.broker.entity.ProductRetryJob;
import com.university.microservices.broker.repository.OrderRetryJobRepository;
import com.university.microservices.broker.repository.PaymentRetryJobRepository;
import com.university.microservices.broker.repository.ProductRetryJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryJobConsumer {

    private final PaymentRetryJobRepository paymentRepo;
    private final OrderRetryJobRepository orderRepo;
    private final ProductRetryJobRepository productRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "payments_retry_jobs", groupId = "broker-message-group")
    public void consumePaymentRetry(String payload) {
        log.info("Received payment retry job: {}", payload);
        PaymentRetryJob job = new PaymentRetryJob();
        job.setRequestData(payload);
        job.setStatus("SCHEDULED");
        job.setReferenceId(extractId(payload, "paymentCode"));
        job.setAction(extractAction(payload, "CREATE_PAYMENT"));
        paymentRepo.save(job);
    }

    @KafkaListener(topics = "order_retry_jobs", groupId = "broker-message-group")
    public void consumeOrderRetry(String payload) {
        log.info("Received order retry job: {}", payload);
        OrderRetryJob job = new OrderRetryJob();
        job.setRequestData(payload);
        job.setStatus("SCHEDULED");
        job.setReferenceId(extractId(payload, "orderCode"));
        job.setAction(extractAction(payload, "CREATE_ORDER"));
        orderRepo.save(job);
    }

    @KafkaListener(topics = "product_retry_jobs", groupId = "broker-message-group")
    public void consumeProductRetry(String payload) {
        log.info("Received product retry job: {}", payload);
        ProductRetryJob job = new ProductRetryJob();
        job.setRequestData(payload);
        job.setStatus("SCHEDULED");
        job.setReferenceId(extractId(payload, "productCode"));
        job.setAction(extractAction(payload, "CREATE_PRODUCT"));
        productRepo.save(job);
    }

    private String extractId(String payload, String key) {
        try {
            JsonNode tree = mapper.readTree(payload);
            if (tree.has(key)) return tree.get(key).asText();
            if (tree.has("id")) return tree.get("id").asText();
        } catch (Exception e) {}
        return "GENERIC-" + System.currentTimeMillis();
    }

    private String extractAction(String payload, String defaultAction) {
        try {
            JsonNode tree = mapper.readTree(payload);
            if (tree.has("action")) return tree.get("action").asText();
        } catch (Exception e) {}
        return defaultAction;
    }
}
