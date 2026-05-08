package com.university.microservices.broker.chain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class RetryContext {
    private final UUID jobId;
    private final String entityType;
    private final String referenceId;
    private final String action;
    
    private Map<String, Object> requestData;
    private Map<String, Object> responseData;
    private String status = "SCHEDULED";
    private final ObjectMapper mapper = new ObjectMapper();

    public RetryContext(UUID jobId, String entityType, String referenceId, String action, String jsonRequest, String jsonResponse) {
        this.jobId = jobId;
        this.entityType = entityType;
        this.referenceId = referenceId;
        this.action = action;
        this.requestData = parseJson(jsonRequest);
        this.responseData = parseJson(jsonResponse);
    }

    private Map<String, Object> parseJson(String json) {
        try {
            if (json != null && !json.trim().isEmpty() && !json.trim().equals("null")) {
                return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {}
        return new HashMap<>();
    }

    public void updateStepStatus(String stepName, String status, String message) {
        Map<String, String> stepResult = new HashMap<>();
        stepResult.put("status", status);
        stepResult.put("message", message);
        this.responseData.put(stepName, stepResult);
    }

    public String getResponseDataAsString() {
        try {
            return mapper.writeValueAsString(this.responseData);
        } catch (Exception e) {
            return "{}";
        }
    }
}
