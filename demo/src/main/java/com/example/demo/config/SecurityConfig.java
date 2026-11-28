package com.example.demo.config;

import com.example.demo.service.seguridad.IntentosLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
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

    @Value("${app.sesion.cliente-minutos:20}")
    private int clienteMinutos;

    @Value("${app.sesion.admin-minutos:10}")
    private int adminMinutos;

    private SecurityContextRepository adminContextRepository() {
        HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
        repo.setSpringSecurityContextKey(ADMIN_CONTEXT_KEY);
        return repo;
    }

    private boolean sesionExpirada(jakarta.servlet.http.HttpServletRequest request) {
        return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
    }

    private AuthenticationEntryPoint clienteEntryPoint() {
        return (request, response, exception) -> {
            String destino = sesionExpirada(request) ? "/cliente/login?expired" : "/cliente/login";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    private AuthenticationEntryPoint adminEntryPoint() {
        return (request, response, exception) -> {
            String destino = sesionExpirada(request) ? "/admin/login?expired" : "/acceso-denegado";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    private AuthenticationSuccessHandler clienteSuccessHandler(IntentosLoginService intentos) {
        SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();
        delegate.setDefaultTargetUrl("/");
        return (request, response, authentication) -> {
            intentos.limpiarCliente(authentication.getName());
            request.getSession().setMaxInactiveInterval(clienteMinutos * 60);
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
            request.getSession().setMaxInactiveInterval(adminMinutos * 60);
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
                        .authenticationEntryPoint(adminEntryPoint())
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
                        .authenticationEntryPoint(clienteEntryPoint())
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