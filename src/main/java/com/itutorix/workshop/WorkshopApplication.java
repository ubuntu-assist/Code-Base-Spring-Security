package com.itutorix.workshop;

import com.itutorix.workshop.auth.AuthenticationService;
import com.itutorix.workshop.auth.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.itutorix.workshop.user.Role.ADMIN;
import static com.itutorix.workshop.user.Role.MANAGER;

@SpringBootApplication
@Slf4j
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class WorkshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkshopApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService authenticationService
	) {
		return args -> {
			RegisterRequest admin = new RegisterRequest(
					"Admin",
					"Admin",
					"admin@test.com",
					"password",
					ADMIN
			);
			log.info("\nAdmin token: " + authenticationService.register(admin).accessToken());

			RegisterRequest manager = new RegisterRequest(
					"Manager",
					"Manager",
					"manager@test.com",
					"password",
					MANAGER
			);
			log.info("\nManager token: " + authenticationService.register(manager).accessToken());
		};
	}
}
