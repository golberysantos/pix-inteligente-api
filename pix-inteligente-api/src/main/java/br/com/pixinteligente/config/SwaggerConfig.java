package br.com.pixinteligente.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger UI via Springdoc OpenAPI.
 *
 * Disponível em: http://localhost:8080/swagger-ui/index.html
 *
 * Configura:
 * - Informações gerais da API (título, versão, descrição)
 * - Esquema de segurança HTTP Basic para autenticação no Swagger UI
 *
 * @author Golbery Santos
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura as informações gerais e o esquema de segurança da API.
     *
     * @return OpenAPI configurado com informações e segurança.
     */
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Pix Inteligente API")
                        .version("1.0.0")
                        .description("""
                                API REST para transferências Pix com validação inteligente via Gemini.
                                
                                Padrões de Projeto aplicados:
                                - Singleton  (Creational): GeminiClient gerenciado pelo Spring
                                - Builder    (Creational): construção da entidade Transacao
                                - Facade     (Structural): PixFacade orquestra o fluxo completo
                                - Adapter    (Structural): PixRepositoryAdapter e GeminiAdapter
                                - Strategy   (Behavioral): validação de limite diurno/noturno
                                - Template Method (Behavioral): ServicoNotificacao
                                
                                Autenticação: HTTP Basic
                                Usuários disponíveis: admin/admin123 | user/user123
                                """)
                        .contact(new Contact()
                                .name("pix-inteligente-api")
                                .url("https://github.com/seu-usuario/pix-inteligente-api")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}
