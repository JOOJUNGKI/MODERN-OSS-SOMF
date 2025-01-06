
package com.workflow.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
   @Bean
   public OpenAPI openAPI() {
       return new OpenAPI()
               .info(new Info()
                       .title("워크플로우 서비스 API")
                       .description("워크플로우 생성 및 이력 조회 API")
                       .version("1.0.0"))
               .servers(List.of(
                       new Server().url("http://localhost:8080").description("로컬 서버")
               ));
   }
}
