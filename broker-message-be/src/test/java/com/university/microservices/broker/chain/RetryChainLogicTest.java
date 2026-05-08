package com.university.microservices.broker.chain;

import com.university.microservices.broker.chain.step.PasoARetryOperationStep;
import com.university.microservices.broker.chain.step.PasoBSendEmailStep;
import com.university.microservices.broker.chain.step.PasoCUpdateDbStep;
import com.university.microservices.broker.chain.step.PasoDCreateMongoRecordStep;
import com.university.microservices.broker.mongo.MongoRetryHistory;
import com.university.microservices.broker.mongo.MongoRetryHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RetryChainLogicTest {

    @InjectMocks
    private PasoARetryOperationStep pasoA;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PasoBSendEmailStep pasoB;

    @InjectMocks
    private PasoCUpdateDbStep pasoC;

    @Mock
    private MongoRetryHistoryRepository mongoRepo;

    @InjectMocks
    private PasoDCreateMongoRecordStep pasoD;

    @BeforeEach
    void setUp() {
        pasoA.setNext(pasoB);
        pasoB.setNext(pasoC);
        pasoC.setNext(pasoD);
    }

    @Test
    void testSuccessfulChain() throws Exception {
        RetryContext context = new RetryContext(UUID.randomUUID(), "PAYMENT", "PAY-123", "CREATE", "{\"data\": {\"id\": \"123\"}}", null);

        // Execute chain starting from A
        pasoA.execute(context);

        // Verify Paso A
        Map<String, Object> response = context.getResponseData();
        assertNotNull(response.get("retryOperation"));
        assertEquals("SUCCESS", ((Map<String, String>) response.get("retryOperation")).get("status"));

        // Verify Paso B
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
        assertNotNull(response.get("sendEmail"));
        assertEquals("SUCCESS", ((Map<String, String>) response.get("sendEmail")).get("status"));

        // Verify Paso C
        assertEquals("SUCCESS", context.getStatus());
        assertNotNull(response.get("updateRetryJobs"));
        assertEquals("SUCCESS", ((Map<String, String>) response.get("updateRetryJobs")).get("status"));

        // Verify Paso D
        verify(mongoRepo, times(1)).save(any(MongoRetryHistory.class));
        assertNotNull(response.get("createMongoRecord"));
        assertEquals("SUCCESS", ((Map<String, String>) response.get("createMongoRecord")).get("status"));
    }

    @Test
    void testFailureInterruptsChain() throws Exception {
        RetryContext context = new RetryContext(UUID.randomUUID(), "ORDER", "ORD-123", "CREATE", "{}", null);

        // Make mailSender throw an exception to simulate Paso B failure
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        try {
            pasoA.execute(context);
        } catch (Exception e) {
            // Expected exception
        }

        // Verify Paso A was successful before Paso B failed
        Map<String, Object> response = context.getResponseData();
        assertNotNull(response.get("retryOperation"));

        // Verify Paso C & D were not executed
        assertEquals("SCHEDULED", context.getStatus());
        verify(mongoRepo, times(0)).save(any(MongoRetryHistory.class));
    }
}
