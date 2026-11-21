package com.example.demo.service.Cliente.impl;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.cliente.TokenRecuperacionEntity;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.TokenRecuperacionRepository;
import com.example.demo.service.Cliente.RecuperacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class RecuperacionServiceImpl implements RecuperacionService {

    private final ClienteRepository clienteRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String baseUrl;
    private final String remitente;
    private final long minutos;
    private final SecureRandom random = new SecureRandom();

    public RecuperacionServiceImpl(ClienteRepository clienteRepository,
                                   TokenRecuperacionRepository tokenRepository,
                                   PasswordEncoder passwordEncoder,
                                   JavaMailSender mailSender,
                                   @Value("${app.base-url}") String baseUrl,
                                   @Value("${app.mail.from}") String remitente,
                                   @Value("${app.reset.minutos:30}") long minutos) {
        this.clienteRepository = clienteRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.baseUrl = baseUrl;
        this.remitente = remitente;
        this.minutos = minutos;
    }

    @Override
    @Transactional
    public void solicitar(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        Optional<ClienteEntity> clienteOpt = clienteRepository.findByCorreo(correo.trim());
        if (clienteOpt.isEmpty()) {
            return;
        }
        ClienteEntity cliente = clienteOpt.get();

        tokenRepository.deleteByCliente(cliente);
        tokenRepository.flush();

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        TokenRecuperacionEntity entity = new TokenRecuperacionEntity();
        entity.setCliente(cliente);
        entity.setTokenHash(hash(token));
        entity.setExpira(LocalDateTime.now().plusMinutes(minutos));
        entity.setUsado(false);
        tokenRepository.save(entity);

        enviarCorreo(cliente, token);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tokenValido(String token) {
        return buscarVigente(token).isPresent();
    }

    @Override
    @Transactional
    public void restablecer(String token, String nuevaPassword) {
        TokenRecuperacionEntity entity = buscarVigente(token)
                .orElseThrow(() -> new BusinessException("El enlace es inválido o ha expirado"));
        ClienteEntity cliente = entity.getCliente();
        cliente.setPassword(passwordEncoder.encode(nuevaPassword));
        clienteRepository.save(cliente);
        entity.setUsado(true);
        tokenRepository.save(entity);
    }

    private Optional<TokenRecuperacionEntity> buscarVigente(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByTokenHash(hash(token))
                .filter(t -> !t.isUsado())
                .filter(t -> t.getExpira().isAfter(LocalDateTime.now()));
    }

    private void enviarCorreo(ClienteEntity cliente, String token) {
        String enlace = baseUrl + "/cliente/restablecer?token=" + token;
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(cliente.getCorreo());
        mensaje.setSubject("K-TARO - Recuperar contraseña");
        mensaje.setText("Hola " + cliente.getNombre() + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n"
                + "Ingresa al siguiente enlace (válido por " + minutos + " minutos):\n\n"
                + enlace + "\n\n"
                + "Si no fuiste tú, ignora este mensaje.\n\n"
                + "Equipo K-TARO");
        try {
            mailSender.send(mensaje);
        } catch (Exception ex) {
            throw new BusinessException("No se pudo enviar el correo. Intenta nuevamente más tarde");
        }
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}