package com.itutorix.workshop.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSenderImpl mailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * Asynchronously sends an email to the specified recipient with a personalized message.
     *
     * @param to the recipient's email address
     * @param username the user's username
     * @param templateName the name of the Thymeleaf template to use for the email content
     * @param confirmationUrl the URL to be included in the email for confirmation or further action
     *
     * @throws MessagingException if an error occurs while sending the email
     */
    @Async
    public void send(
            String to,
            String username,
            String templateName,
            String confirmationUrl
    ) throws MessagingException {
        if(!StringUtils.hasLength(confirmationUrl)) {
            confirmationUrl = "confirm-email";
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("fopaduclair2000@gmail.com");
        helper.setTo(to);
        helper.setSubject("Welcome to Ubuntu Assist");

        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }
}
