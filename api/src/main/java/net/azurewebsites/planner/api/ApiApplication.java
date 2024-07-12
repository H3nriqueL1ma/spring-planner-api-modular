package net.azurewebsites.planner.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Objects;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "net.azurewebsites.planner")
@EntityScan(basePackages = "net.azurewebsites.planner")
public class ApiApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("spring.datasource.username", Objects.requireNonNull(dotenv.get("POSTGRES_USERNAME")));
        System.setProperty("spring.datasource.password", Objects.requireNonNull(dotenv.get("POSTGRES_PASSWORD")));
        System.setProperty("spring.mail.username", Objects.requireNonNull(dotenv.get("SMTP_USERNAME")));
        System.setProperty("spring.mail.password", Objects.requireNonNull(dotenv.get("SMTP_PASSWORD")));

        SpringApplication.run(ApiApplication.class, args);
    }

}
