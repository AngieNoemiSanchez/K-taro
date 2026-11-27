package com.example.demo.config;

import com.example.demo.service.seguridad.IntentosLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ADMIN_CONTEXT_KEY = "ADMIN_SPRING_SECURITY_CONTEXT";

    private SecurityContextRepository adminContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey(ADMIN_CONTEXT_KEY);
        return repo;
    }

    private AuthenticationSuccessHandler clienteSuccessHandler(IntentosLoginService intentos) {
        SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();
        delegate.setDefaultTargetUrl("/");
        return (request, response, authentication) -> {
            intentos.limpiarCliente(authentication.getName());
            delegate.onAuthenticationSuccess(request, response, authentication);
        };
    }

    private AuthenticationFailureHandler clienteFailureHandler(IntentosLoginService intentos) {
        return (request, response, exception) -> {
            String correo = request.getParameter("username");
            if (exception instanceof BadCredentialsException) {
                intentos.registrarFalloCliente(correo);
            }
            long minutos = intentos.minutosRestantesCliente(correo);
            String destino = minutos > 0 ? "/cliente/login?locked=" + minutos : "/cliente/login?error";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    private AuthenticationSuccessHandler adminSuccessHandler(IntentosLoginService intentos) {
        SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();
        delegate.setDefaultTargetUrl("/admin/dashboard");
        delegate.setAlwaysUseDefaultTargetUrl(true);
        return (request, response, authentication) -> {
            intentos.limpiarAdmin(authentication.getName());
            delegate.onAuthenticationSuccess(request, response, authentication);
        };
    }

    private AuthenticationFailureHandler adminFailureHandler(IntentosLoginService intentos) {
        return (request, response, exception) -> {
            String correo = request.getParameter("username");
            if (exception instanceof BadCredentialsException) {
                intentos.registrarFalloAdmin(correo);
            }
            long minutos = intentos.minutosRestantesAdmin(correo);
            String destino = minutos > 0 ? "/admin/login?locked=" + minutos : "/admin/login?error";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                                                        AdminUserDetailsService adminUserDetailsService,
                                                        IntentosLoginService intentosLoginService) {
        http
                .securityMatcher("/admin/**", "/js/admin-pedidos.js")
                .userDetailsService(adminUserDetailsService)
                .securityContext(ctx -> ctx.securityContextRepository(adminContextRepository()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login", "/css/admin-login.css").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .successHandler(adminSuccessHandler(intentosLoginService))
                        .failureHandler(adminFailureHandler(intentosLoginService))
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/acceso-denegado"))
                        .accessDeniedPage("/acceso-denegado")
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                        .invalidateHttpSession(false)
                        .addLogoutHandler((request, response, auth) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) session.removeAttribute(ADMIN_CONTEXT_KEY);
                        })
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain clienteSecurityFilterChain(HttpSecurity http,
                                                          CustomUserDetailsService customUserDetailsService,
                                                          IntentosLoginService intentosLoginService) {
        http
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/cliente/perfil", "/cliente/perfil/**",
                                "/pago", "/compra-exitosa", "/compra-exitosa/**",
                                "/api/pedidos/**", "/api/carrito/**").hasRole("CLIENTE")
                        .requestMatchers("/acceso-denegado", "/error/**").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/cliente/login")
                        .loginProcessingUrl("/login")
                        .successHandler(clienteSuccessHandler(intentosLoginService))
                        .failureHandler(clienteFailureHandler(intentosLoginService))
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acceso-denegado")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/cliente/login?logout")
                        .invalidateHttpSession(false)
                        .addLogoutHandler((request, response, auth) -> {
                            HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
                            }
                        })
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}