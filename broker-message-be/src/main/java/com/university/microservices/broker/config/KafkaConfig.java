package com.university.microservices.broker.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic paymentsRetryJobsTopic() {
        return TopicBuilder.name("payments_retry_jobs")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderRetryJobsTopic() {
        return TopicBuilder.name("order_retry_jobs")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productRetryJobsTopic() {
        return TopicBuilder.name("product_retry_jobs")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderStatusChangedTopic() {
        return TopicBuilder.name("order_status_changed_events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic inventoryUpdateTopic() {
        return TopicBuilder.name("inventory_update_events")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentReceivedTopic() {
        return TopicBuilder.name("payment_received_events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
