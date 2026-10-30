package com.example.demo.config;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.repository.ClienteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;

    public CustomUserDetailsService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        ClienteEntity cliente = clienteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente no encontrado con el correo: " + correo));

        return User.builder()
                .username(cliente.getCorreo())
                .password(cliente.getPassword())
                .roles("CLIENTE")
                .build();
    }
}