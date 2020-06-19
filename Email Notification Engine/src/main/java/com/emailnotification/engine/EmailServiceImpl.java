package com.emailnotification.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(EmailRequest emailRequest) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        String[] to = new String[emailRequest.getTo().size()];
        to = emailRequest.getTo().toArray(to);
        mimeMessageHelper.setFrom(emailRequest.getFrom());
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(emailRequest.getSubject());

        if(emailRequest.getCc()!=null && !emailRequest.getCc().isEmpty()){
            String[] cc = new String[emailRequest.getCc().size()];
            cc = emailRequest.getCc().toArray(cc);
        }

        if(emailRequest.getBcc()!=null && !emailRequest.getBcc().isEmpty()){
            String[] bcc = new String[emailRequest.getBcc().size()];
            bcc = emailRequest.getBcc().toArray(bcc);
        }
        mimeMessageHelper.setText(emailRequest.getBody() == null ? "": emailRequest.getBody(),
                (emailRequest.getEmailFormat() == EmailFormat.HTML));
        javaMailSender.send(mimeMessage);
    }
}
