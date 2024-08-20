package com.itutorix.workshop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * This class is responsible for configuring the OpenAPI specification for the Spring Security application.
 * It provides information about the API, its endpoints, and the security requirements.
 *
 * @author Duclair FOPA KUETE
 * @email duclair.fopa@kimbocare.com
 * @url <a href="https://fkd.netlify.app">...</a>
 * @version 1.0
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Duclair FOPA KUETE",
                        email = "duclair.fopa@kimbocare.com",
                        url = "https://fkd.netlify.app"
                ),
                description = "OpenAPI Documentation for Ubuntu Assist",
                title = "OpenAPI Specification",
                license = @License(
                        url = "https://something.com",
                        name = "MIT"
                ),
                version = "1.0"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Env"
                ),
                @Server(
                        url = "https://fkd.netlify.app",
                        description = "Prod Env"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        description = "JWT auth description",
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
