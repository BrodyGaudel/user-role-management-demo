package org.mounanga.userservice.util.implementation;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.mounanga.userservice.configuration.ApplicationProperties;
import org.mounanga.userservice.util.MailingService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Slf4j
@Service
public class MailingServiceImpl implements MailingService {

    private static final String NOTIFICATION_TEMPLATE = "notification.html";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ApplicationProperties applicationProperties;

    public MailingServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine, ApplicationProperties applicationProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.applicationProperties = applicationProperties;
    }

    @Async
    @Override
    public void sendMail(String to, String subject, String body) {
        log.info("In sendMail()");
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE_MIXED, UTF_8.name());
            Map<String, Object> properties = new HashMap<>();
            properties.put("data", body);
            Context context = new Context();
            context.setVariables(properties);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(applicationProperties.getEmailSystem());
            String template = templateEngine.process(NOTIFICATION_TEMPLATE, context);
            helper.setText(template, true);
            mailSender.send(mimeMessage);
            log.info("Mail sent to {} successfully", to);
        }catch (Exception e){
            log.warn("Failed to send mail to '{}'", to);
            log.error(e.getMessage());
            log.error(e.getLocalizedMessage());
        }
    }
}
