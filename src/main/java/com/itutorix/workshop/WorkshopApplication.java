package com.itutorix.workshop;

import com.github.javafaker.Faker;
import com.itutorix.workshop.auth.AuthenticationService;
import com.itutorix.workshop.auth.RegisterRequest;
import com.itutorix.workshop.author.Author;
import com.itutorix.workshop.author.AuthorRepository;
import com.itutorix.workshop.resource.Video;
import com.itutorix.workshop.resource.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

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
			AuthenticationService authenticationService,
			AuthorRepository authorRepository
	) {
		Faker faker = new Faker();

		return args -> {
			RegisterRequest admin = new RegisterRequest(
					"Admin",
					"Admin",
					"admin@test.com",
					"password",
					ADMIN
			);
            log.info("\nAdmin token: {}", authenticationService.register(admin).accessToken());

			RegisterRequest manager = new RegisterRequest(
					"Manager",
					"Manager",
					"manager@test.com",
					"password",
					MANAGER
			);
            log.info("\nManager token: {}", authenticationService.register(manager).accessToken());

			for (int i = 0; i < 50; i++) {
				Author author = Author.builder()
						.firstName(faker.name().firstName())
						.lastName(faker.name().lastName())
						.age(faker.number().numberBetween(18, 50))
						.email(faker.name().username() + "@gmail.com")
						.build();
				authorRepository.save(author);
			}
			authorRepository.updateAuthor(25, 2);

			log.info("Find by named query");
			authorRepository.findByNamedQuery(25)
					.forEach(author -> log.info(author.toString()));
		};
	}
}
