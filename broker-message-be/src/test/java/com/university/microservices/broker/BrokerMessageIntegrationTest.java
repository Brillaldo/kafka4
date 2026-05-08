package com.university.microservices.broker;

import com.university.microservices.broker.entity.PaymentRetryJob;
import com.university.microservices.broker.repository.PaymentRetryJobRepository;
import com.university.microservices.broker.mongo.MongoRetryHistory;
import com.university.microservices.broker.mongo.MongoRetryHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class BrokerMessageIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
            .withDatabaseName("broker_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @Container
    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("app.scheduler.retry-delay-ms", () -> "2000"); // Fast scheduler for tests
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private PaymentRetryJobRepository repository;

    @Autowired
    private MongoRetryHistoryRepository mongoRepository;

    @Test
    void testFullRetryLifecycle() {
        // 1. Send message to Kafka
        String payload = "{\"paymentCode\": \"PAY-999\"}";
        kafkaTemplate.send("payments_retry_jobs", payload);

        // 2. Wait for consumer to save it as SCHEDULED and then Scheduler to process it up to SUCCESS
        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            List<PaymentRetryJob> jobs = repository.findAll();
            assertEquals(1, jobs.size());
            PaymentRetryJob job = jobs.get(0);
            
            // Check if scheduler has already processed it
            assertEquals("SUCCESS", job.getStatus());
            
            // Check if JSON payload was updated by the chain
            assertTrue(job.getResponseData().contains("retryOperation"));
            assertTrue(job.getResponseData().contains("sendEmail"));
            assertTrue(job.getResponseData().contains("updateRetryJobs"));
            assertTrue(job.getResponseData().contains("createMongoRecord"));

            // Verification of Paso D
            List<MongoRetryHistory> mongoRecords = mongoRepository.findAll();
            assertEquals(1, mongoRecords.size());
            assertEquals("PAY-999", mongoRecords.get(0).getReferenceId());
            assertEquals("SUCCESS", mongoRecords.get(0).getFinalStatus());
        });
    }
}
