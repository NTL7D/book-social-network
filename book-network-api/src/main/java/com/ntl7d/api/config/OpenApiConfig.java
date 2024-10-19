package com.ntl7d.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(
    contact = @Contact(
    name = "NTL7D", 
    email = "nguyenthanhlong528@gmail.com"), 
    title = "OpenAPI specification", 
    description = "OpenAPI documentation for Spring security", 
    version = "1.0.0", 
    license = @License(
        name = "License", 
        url = "http://licensename.com"), 
        termsOfService = "https://example.com/terms"), 
        servers = {
        @Server(description = "Local ENV", url = "http://localhost:5000/api/v1"),
        @Server(description = "Production ENV", url = "https://example.com"),
        }, 
    security = {
        @SecurityRequirement(name = "bearerAuth")
    })
@SecurityScheme(
    name = "bearerAuth", 
    description = "JWT auth", 
    scheme = "bearer", 
    type = SecuritySchemeType.HTTP, 
    bearerFormat = "JWT", 
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

}
