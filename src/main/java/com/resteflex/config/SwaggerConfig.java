package com.resteflex.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ResteFlex Listings API")
                        .version("1.0.0")
                        .description("API pour la gestion des logements Airbnb et réservations avec paiement Stripe et synchronisation iCal")
                        .contact(new Contact()
                                .name("ResteFlex Team")
                                .email("contact@resteflex.fr")
                                .url("https://resteflex-conciergerie.fr")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("Local"),
                        new Server().url("https://api.resteflex.fr/api").description("Production")));
    }
}
