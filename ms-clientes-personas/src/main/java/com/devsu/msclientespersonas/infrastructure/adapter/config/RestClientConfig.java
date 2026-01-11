package com.devsu.msclientespersonas.infrastructure.adapter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.external.cuentas-service.url:http://ms-cuentas-movimientos:8082}")
    private String cuentasServiceUrl;

    @Bean
    public RestClient cuentasRestClient() {
        return RestClient.builder()
                .baseUrl(cuentasServiceUrl)
                .build();
    }
}
