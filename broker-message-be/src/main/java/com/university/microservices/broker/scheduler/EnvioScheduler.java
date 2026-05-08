package com.university.microservices.broker.scheduler;

import com.university.microservices.broker.entity.Envio;
import com.university.microservices.broker.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvioScheduler {

    private final EnvioRepository envioRepository;
    private final JavaMailSender mailSender;

    @Scheduled(fixedRate = 10000) // Cada 10 segundos
    public void processPendingShipments() {
        log.info("Checking for pending shipments...");
        List<Envio> pendingEnvios = envioRepository.findByStatus("PENDING");
        
        for (Envio envio : pendingEnvios) {
            try {
                log.info("Processing shipment for order: {}", envio.getOrdenId());
                
                // 1. Enviar correo de confirmación de envío
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("noreply@university.com");
                message.setTo("cliente@university.com");
                message.setSubject("Su orden está en camino");
                message.setText("Confirmamos que la orden " + envio.getOrdenId() + " ha sido enviada.");
                mailSender.send(message);

                // 2. Actualizar estatus
                envio.setStatus("SENT");
                envio.setSentAt(LocalDateTime.now());
                envioRepository.save(envio);
                
                log.info("Shipment for order {} processed successfully", envio.getOrdenId());
            } catch (Exception e) {
                log.error("Error processing shipment for order: {}", envio.getOrdenId(), e);
            }
        }
    }
}
