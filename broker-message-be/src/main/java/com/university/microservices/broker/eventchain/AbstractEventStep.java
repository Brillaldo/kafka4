package com.university.microservices.broker.eventchain;

public abstract class AbstractEventStep implements EventStep {
    protected EventStep next;

    @Override
    public void setNext(EventStep nextStep) {
        this.next = nextStep;
    }

    @Override
    public void execute(EventContext context) throws Exception {
        if (!context.isContinueChain()) return;
        process(context);
        if (next != null && context.isContinueChain()) {
            next.execute(context);
        }
    }

    protected abstract void process(EventContext context) throws Exception;
}
