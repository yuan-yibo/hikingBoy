package com.hiking.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger 配置
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("徒步记录 API")
                        .version("1.0.0")
                        .description("徒步记录后端服务 API 文档")
                        .contact(new Contact()
                                .name("Hiking Team")
                                .email("hiking@example.com")));
    }
}
