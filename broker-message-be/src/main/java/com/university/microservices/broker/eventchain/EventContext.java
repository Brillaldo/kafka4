package com.university.microservices.broker.eventchain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class EventContext {
    private final String topic;
    private final String payload;
    private Map<String, Object> data;
    private boolean continueChain = true;
    private final ObjectMapper mapper = new ObjectMapper();

    public EventContext(String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
        try {
            this.data = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            this.data = new HashMap<>();
        }
    }
}
