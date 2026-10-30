package com.example.demo.config;

import com.example.demo.mapper.AdministradorMapper;
import com.example.demo.repository.AdministradorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {
    private final AdministradorRepository repo;
    private final AdministradorMapper mapper;

    public AdminUserDetailsService(AdministradorRepository repo, AdministradorMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        return repo.findByCorreoIgnoreCase(correo.trim())
                .map(mapper::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador no encontrado"));
    }
}