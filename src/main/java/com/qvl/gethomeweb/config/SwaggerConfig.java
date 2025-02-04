package com.qvl.gethomeweb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // 路人可以連的API
    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/public/**")
                .build();
    }

    // 需要登入才可以連的API
    @Bean
    GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth-apis")
                .pathsToMatch("/api/**")
                .build();
    }

    // 房東可以連的api
    @Bean
    GroupedOpenApi landlordApi() {
        return GroupedOpenApi.builder()
                .group("landlords-apis")
                .pathsToMatch("/landlords/**")
                .build();
    }

    // 租客可以連的api
    @Bean
    GroupedOpenApi tenantApi() {
        return GroupedOpenApi.builder()
                .group("tenants-apis")
                .pathsToMatch("/tenants/**")
                .build();
    }

    // 設定API文件資訊
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Get Home Web API").version("1.0.0").description("抵家租屋網 API文件"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));

    }
}
