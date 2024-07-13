package net.azurewebsites.planner.api.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.azurewebsites.planner.core.Models.EmailModel;
import net.azurewebsites.planner.core.Models.TripMigrationModel;
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
import java.util.UUID;

@Service
public class EmailService {
    private static final String SPRING_LOGO_IMAGE = "templates/images/logo-planner.png";
    private static final String PNG_MIME = "image/png";

    private final Environment environment;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(Environment environment, JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.environment = environment;
        this.mailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendEmail(EmailModel emailModel, TripMigrationModel data, UUID id, String mailSubject, String templateName) throws UnsupportedEncodingException, MessagingException {
        final Context context = new Context(LocaleContextHolder.getLocale());

        String departureDate = data.getStartsAt().getDayOfMonth() + "/" + data.getStartsAt().getMonthValue() + "/" + data.getStartsAt().getYear();
        String returnDate = data.getEndsAt().getDayOfMonth() + "/" + data.getEndsAt().getMonthValue() + "/" + data.getEndsAt().getYear();

        context.setVariable("name", emailModel.getTripCreatorName());
        context.setVariable("destination", data.getDestination());
        context.setVariable("departureDate", departureDate);
        context.setVariable("returnDate", returnDate);
        context.setVariable("tripId", id);
        context.setVariable("springLogo", SPRING_LOGO_IMAGE);

        String emailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String emailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        email.setTo(emailModel.getTripCreatorEmail());
        email.setSubject(mailSubject);
        email.setFrom(new InternetAddress(emailFrom, emailFromName));

        final String htmlContent = this.templateEngine.process(templateName, context);

        email.setText(htmlContent, true);

        ClassPathResource classPathResource = new ClassPathResource(SPRING_LOGO_IMAGE);

        email.addInline("springLogo", classPathResource, PNG_MIME);

        mailSender.send(mimeMessage);
    }
}
