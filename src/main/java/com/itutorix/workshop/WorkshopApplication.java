package com.itutorix.workshop;

import com.itutorix.workshop.auth.AuthenticationService;
import com.itutorix.workshop.auth.RegisterRequest;
import com.itutorix.workshop.user.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.itutorix.workshop.user.Role.ADMIN;
import static com.itutorix.workshop.user.Role.MANAGER;

@SpringBootApplication
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
			System.out.println("Admin token: " + authenticationService.register(admin).accessToken());

			RegisterRequest manager = new RegisterRequest(
					"Manager",
					"Manager",
					"manager@test.com",
					"password",
					MANAGER
			);
			System.out.println("Manager token: " + authenticationService.register(manager).accessToken());
		};
	}
}
