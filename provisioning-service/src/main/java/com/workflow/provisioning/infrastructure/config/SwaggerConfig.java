
package com.workflow.provisioning.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("워크플로우 ACL 서비스 API")
                        .description("레거시 시스템 연동을 위한 Anti-Corruption Layer API")
                        .version("1.0.0"));
    }
}
