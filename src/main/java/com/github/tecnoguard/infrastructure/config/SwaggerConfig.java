package com.github.tecnoguard.infrastructure.config;

import com.github.tecnoguard.core.dto.ErrorResponseDTO;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Tecnoguard API")
                        .version("1.0")
                        .description("Sistema de Gestão de Manutenção Industrial")
                )
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                        )
                        .addExamples("401", new Example()
                                .summary("Erro: Falha de Autenticação")
                                .value("""
                                        {
                                          "timestamp": "2025-11-19T15:00:00.000",
                                          "error": "Autenticação",
                                          "message": "Acesso Negado. Usuário Não Autenticado",
                                          "path": "{URL_DA_REQUISIÇÃO}"
                                        }
                                        """)
                        )
                        .addExamples("403", new Example()
                                .summary("Erro: Falha de Autorização")
                                .value("""
                                        {
                                          "timestamp": "2025-11-19T15:00:00.000",
                                          "error": "Autorização",
                                          "message": "Acesso Negado. Usuário não tem Permissão para essa ação.",
                                          "path": "{URL_DA_REQUISIÇÃO}"
                                        }
                                        """)
                        )
                        .addExamples("404", new Example()
                                .summary("Erro: Não Encontrado")
                                .value("""
                                        {
                                          "timestamp": "2025-11-19T15:00:00.000",
                                          "error": "Não Encontrado",
                                          "message": "O que você procurou não foi encontrado.",
                                          "path": "{URL_DA_REQUISIÇÃO}"
                                        }
                                        """)
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                );
    }
}
