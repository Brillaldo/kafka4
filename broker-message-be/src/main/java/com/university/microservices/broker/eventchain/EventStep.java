package com.university.microservices.broker.eventchain;

public interface EventStep {
    void execute(EventContext context) throws Exception;
    void setNext(EventStep nextStep);
}
