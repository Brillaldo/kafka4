package com.university.microservices.broker.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "retry_history")
public class MongoRetryHistory {

    @Id
    private String id;
    private UUID postgresJobId;
    private String entityType;
    private String referenceId;
    private String action;
    private Map<String, Object> requestData;
    private Map<String, Object> responseData;
    private String finalStatus;
    private LocalDateTime executionTime;

}
