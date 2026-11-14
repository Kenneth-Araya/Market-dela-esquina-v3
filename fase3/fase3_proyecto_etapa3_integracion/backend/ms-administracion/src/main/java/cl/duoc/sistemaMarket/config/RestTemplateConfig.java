package cl.duoc.sistemaMarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Permite que los microservicios realicen solicitudes HTTP
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}