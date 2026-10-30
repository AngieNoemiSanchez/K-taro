package com.example.demo.service.Cliente.impl;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.dto.PerfilForm;
import com.example.demo.dto.RegistroForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ClienteMapper;
import com.example.demo.model.cliente.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.Cliente.ClienteService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Cliente crearCliente(RegistroForm form) {
        ValidarCampos(form);
        ClienteEntity entity = clienteMapper.toEntity(form);
        entity.setPassword(passwordEncoder.encode(form.getPassword()));
        return clienteMapper.toModel(clienteRepository.save(entity));
    }

    @Override
    @Transactional
    public Cliente actualizarDatos(Long idCliente, PerfilForm form) {
        ClienteEntity entity = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        entity.setNombre(form.getNombre());
        entity.setApellidos(form.getApellidos());
        entity.setTelefono(form.getTelefono());
        entity.setDireccion(form.getDireccion());

        return clienteMapper.toModel(clienteRepository.save(entity));
    }

    @Override
    public Optional<Cliente> buscarPorCorreo(String correo) {
        return clienteRepository.findByCorreo(correo)
                .map(clienteMapper::toModel);
    }

    private void ValidarCampos(RegistroForm form) {
        if (clienteRepository.existsByCorreo(form.getCorreo())) {
            throw new BusinessException("El correo ya está registrado");
        }
        if (clienteRepository.existsByDni(form.getDni())) {
            throw new BusinessException("El DNI ya está registrado");
        }
        if (Period.between(form.getFechaNacimiento(), LocalDate.now()).getYears() < 18) {
            throw new BusinessException("Debes ser mayor de 18 años para registrarte");
        }
    }
}