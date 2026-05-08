package com.university.microservices.broker.chain.step;

import com.university.microservices.broker.chain.RetryContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasoBSendEmailStep extends AbstractRetryStep {

    private final JavaMailSender mailSender;

    @Override
    protected void process(RetryContext context) throws Exception {
        log.info("PASO B: Sending email notification for entity: {} with id: {}", 
                 context.getEntityType(), context.getJobId());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@university.com");
            message.setTo("admin@university.com");
            String status = context.getStatus() != null ? context.getStatus() : "UNKNOWN";
            message.setSubject("Retry " + status + " for " + context.getEntityType());
            if ("SUCCESS".equals(status)) {
                message.setText("The retry for job " + context.getJobId() + " was successful.");
            } else {
                message.setText("The retry for job " + context.getJobId() + " failed during execution.");
            }

            mailSender.send(message);
            context.updateStepStatus("sendEmail", "SUCCESS", "Email sent to admin@university.com");
            log.info("PASO B: Success");
        } catch (Exception e) {
            log.error("PASO B: Error sending email", e);
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }
}
