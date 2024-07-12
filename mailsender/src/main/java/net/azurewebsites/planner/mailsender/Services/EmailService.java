package net.azurewebsites.planner.mailsender.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private static final String TEMPLATE_NAME_CONFIRM = "confirm-email";
    private static final String TEMPLATE_NAME_CONFIRMED = "confirmed-email";
    private static final String SPRING_LOGO_IMAGE = "templates/images/logo-planner.png";
    private static final String PNG_MIME = "image/png";
    private static final String MAIL_SUBJECT_CONFIRM = "Confirme sua viagem! Plann.er";

    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(Environment environment, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.environment = environment;
        this.mailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmailConfirm(EmailModel emailModel) throws UnsupportedEncodingException, MessagingException {
        String confirmationUrl = "generated_confirmation_url";
        String emailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String emailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        email.setTo("henriquecarlos361893@gmail.com");
        email.setSubject(MAIL_SUBJECT);
        email.setFrom(new InternetAddress(emailFrom, emailFromName));

        final Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariable("email", emailModel.getClientEmail());
        context.setVariable("name", emailModel.getClientName());
        context.setVariable("message", emailModel.getClientMessage());
        context.setVariable("springLogo", SPRING_LOGO_IMAGE);
        context.setVariable("url", confirmationUrl);

        final String htmlContent = this.templateEngine.process(TEMPLATE_NAME, context);

        email.setText(htmlContent, true);

        ClassPathResource classPathResource = new ClassPathResource(SPRING_LOGO_IMAGE);

        email.addInline("springLogo", classPathResource, PNG_MIME);

        mailSender.send(mimeMessage);
    }
}
