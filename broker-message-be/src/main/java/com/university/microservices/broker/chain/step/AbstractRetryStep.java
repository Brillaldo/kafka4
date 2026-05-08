package com.university.microservices.broker.chain.step;

import com.university.microservices.broker.chain.RetryContext;
import com.university.microservices.broker.chain.RetryStep;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRetryStep implements RetryStep {
    private RetryStep nextStep;

    @Override
    public void setNext(RetryStep nextStep) {
        this.nextStep = nextStep;
    }

    @Override
    public void execute(RetryContext context) throws Exception {
        process(context);
        if (nextStep != null) {
            nextStep.execute(context);
        }
    }

    protected abstract void process(RetryContext context) throws Exception;
}
