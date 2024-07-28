package org.mounanga.userservice.util;

public interface MailingService {
    void sendMail(String to, String subject, String body);
}
