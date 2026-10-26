package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso libre a los recursos estáticos y páginas públicas
                .requestMatchers("/", "/inicio", "/productos/**", "/css/**", "/js/**", "/images/**", "/login", "/registro").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // <--- ESTA LÍNEA INDICA A SPRING USAR TU RUTA /login
                .defaultSuccessUrl("/", true)
                .permitAll()
            );

        return http.build();
    }
}
    

