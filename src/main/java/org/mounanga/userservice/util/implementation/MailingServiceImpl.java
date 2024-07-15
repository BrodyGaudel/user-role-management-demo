package org.mounanga.userservice.util.implementation;

import lombok.extern.slf4j.Slf4j;
import org.mounanga.userservice.util.MailingService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailingServiceImpl implements MailingService {
    @Override
    public void sendMail(String to, String subject, String body) {
        log.info("Sending mail to {}", to);
    }
}
