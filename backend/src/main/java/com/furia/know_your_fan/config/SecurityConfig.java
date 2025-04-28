package com.furia.know_your_fan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desativa CSRF (para facilitar testes)
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()  // Libera tudo sem autenticação
                );
        return http.build();
    }
}
