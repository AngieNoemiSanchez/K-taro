package com.example.demo.service.seguridad;

import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class IntentosLoginService {

    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final int maxIntentos;
    private final long bloqueoMinutos;

    public IntentosLoginService(ClienteRepository clienteRepository,
                                AdministradorRepository administradorRepository,
                                @Value("${app.login.max-intentos:5}") int maxIntentos,
                                @Value("${app.login.bloqueo-minutos:15}") long bloqueoMinutos) {
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.maxIntentos = maxIntentos;
        this.bloqueoMinutos = bloqueoMinutos;
    }

    @Transactional
    public void registrarFalloCliente(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        clienteRepository.findByCorreo(correo.trim()).ifPresent(c -> {
            LocalDateTime ahora = LocalDateTime.now();
            if (c.getBloqueadoHasta() != null && !c.getBloqueadoHasta().isAfter(ahora)) {
                c.setBloqueadoHasta(null);
                c.setIntentosFallidos(0);
            }
            c.setIntentosFallidos(c.getIntentosFallidos() + 1);
            if (c.getIntentosFallidos() >= maxIntentos) {
                c.setBloqueadoHasta(ahora.plusMinutes(bloqueoMinutos));
                c.setIntentosFallidos(0);
            }
            clienteRepository.save(c);
        });
    }

    @Transactional
    public void limpiarCliente(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        clienteRepository.findByCorreo(correo.trim()).ifPresent(c -> {
            if (c.getIntentosFallidos() > 0 || c.getBloqueadoHasta() != null) {
                c.setIntentosFallidos(0);
                c.setBloqueadoHasta(null);
                clienteRepository.save(c);
            }
        });
    }

    @Transactional(readOnly = true)
    public long minutosRestantesCliente(String correo) {
        if (correo == null || correo.isBlank()) {
            return 0;
        }
        return clienteRepository.findByCorreo(correo.trim())
                .map(c -> minutos(c.getBloqueadoHasta()))
                .orElse(0L);
    }

    @Transactional
    public void registrarFalloAdmin(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        administradorRepository.findByCorreoIgnoreCase(correo.trim()).ifPresent(a -> {
            LocalDateTime ahora = LocalDateTime.now();
            if (a.getBloqueadoHasta() != null && !a.getBloqueadoHasta().isAfter(ahora)) {
                a.setBloqueadoHasta(null);
                a.setIntentosFallidos(0);
            }
            a.setIntentosFallidos(a.getIntentosFallidos() + 1);
            if (a.getIntentosFallidos() >= maxIntentos) {
                a.setBloqueadoHasta(ahora.plusMinutes(bloqueoMinutos));
                a.setIntentosFallidos(0);
            }
            administradorRepository.save(a);
        });
    }

    @Transactional
    public void limpiarAdmin(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        administradorRepository.findByCorreoIgnoreCase(correo.trim()).ifPresent(a -> {
            if (a.getIntentosFallidos() > 0 || a.getBloqueadoHasta() != null) {
                a.setIntentosFallidos(0);
                a.setBloqueadoHasta(null);
                administradorRepository.save(a);
            }
        });
    }

    @Transactional(readOnly = true)
    public long minutosRestantesAdmin(String correo) {
        if (correo == null || correo.isBlank()) {
            return 0;
        }
        return administradorRepository.findByCorreoIgnoreCase(correo.trim())
                .map(a -> minutos(a.getBloqueadoHasta()))
                .orElse(0L);
    }

    private long minutos(LocalDateTime hasta) {
        if (hasta == null) {
            return 0;
        }
        long segundos = Duration.between(LocalDateTime.now(), hasta).getSeconds();
        return segundos <= 0 ? 0 : (segundos + 59) / 60;
    }
}