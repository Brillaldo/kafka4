package com.university.microservices.broker.chain;

public interface RetryStep {
    void execute(RetryContext context) throws Exception;
    void setNext(RetryStep nextStep);
}
