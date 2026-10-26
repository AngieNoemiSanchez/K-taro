package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/inicio", "/productos/**", "/css/**", "/js/**", "/images/**", "/login", "/registro").permitAll()
                        .anyRequest().permitAll()   // TEMPORAL: solo para las capturas
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                );

        return http.build();
    }

    // TEMPORAL: usuarios de prueba
    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                User.withUsername("cliente@ktaro.com")
                        .password("{noop}1234")
                        .roles("USER")
                        .build(),
                User.withUsername("admin@ktaro.com")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build());
    }
}

