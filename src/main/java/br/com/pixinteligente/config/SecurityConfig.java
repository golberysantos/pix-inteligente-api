package br.com.pixinteligente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança da aplicação.
 *
 * Padrão aplicado: Singleton (Creational)
 * Os beans declarados aqui são gerenciados pelo Spring como
 * singletons — uma única instância por contexto de aplicação.
 *
 * Estratégia: HTTP Basic Authentication
 * Simples e suficiente para o desafio — protege todos os endpoints
 * da API sem a complexidade de JWT.
 *
 * Usuários disponíveis:
 * - admin / admin123 → perfil ADMIN
 * - user  / user123  → perfil USER
 *
 * Endpoints liberados sem autenticação:
 * - /swagger-ui/**     → documentação da API
 * - /v3/api-docs/**    → spec OpenAPI
 * - /h2-console/**     → console do banco H2
 *
 * @author Golbery Santos
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura a cadeia de filtros de segurança.
     *
     * @param http Objeto de configuração do Spring Security.
     * @return SecurityFilterChain configurado.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // necessário para o H2 Console
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos — documentação e console H2
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/h2-console/**"
                ).permitAll()
                // Todos os demais endpoints exigem autenticação
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {}); // HTTP Basic Authentication

        return http.build();
    }

    /**
     * Configura os usuários em memória para autenticação.
     * Em produção, substituir por UserDetailsService com banco de dados.
     *
     * @param encoder PasswordEncoder para criptografar as senhas.
     * @return UserDetailsService com os usuários configurados.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * Bean de criptografia de senhas com BCrypt.
     * BCrypt é o algoritmo recomendado pelo Spring Security.
     *
     * @return PasswordEncoder BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
