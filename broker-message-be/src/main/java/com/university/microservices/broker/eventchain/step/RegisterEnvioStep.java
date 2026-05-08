package com.university.microservices.broker.eventchain.step;

import com.university.microservices.broker.entity.Envio;
import com.university.microservices.broker.eventchain.AbstractEventStep;
import com.university.microservices.broker.eventchain.EventContext;
import com.university.microservices.broker.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterEnvioStep extends AbstractEventStep {

    private final EnvioRepository envioRepository;

    @Override
    protected void process(EventContext context) throws Exception {
        String ordenId = (String) context.getData().get("id");
        if (ordenId == null) {
            ordenId = (String) context.getData().get("ordenId");
        }
        
        log.info("Registering shipment for order: {}", ordenId);
        Envio envio = new Envio(ordenId);
        envioRepository.save(envio);
        log.info("Shipment registered successfully");
    }
}
