package com.emailnotification.engine;

import javax.mail.MessagingException;

public interface EmailService {

    void sendMail(EmailRequest emailRequest) throws MessagingException;
}
