package com.emailnotification.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value="/api/emailengine")
public class EmailNotificationEngineController {

    @Autowired
    EmailService emailService;
    private final static Logger logger = LoggerFactory.getLogger(EmailNotificationEngineController.class);

    @PostMapping(value="/sendMail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void email(@Validated @RequestBody final EmailRequest emailRequest) throws MessagingException {


        emailService.sendMail(emailRequest);
    }

}
