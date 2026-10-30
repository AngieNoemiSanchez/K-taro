package com.example.demo.service.Cliente;

import com.example.demo.dto.PerfilForm;
import com.example.demo.dto.RegistroForm;
import com.example.demo.model.cliente.Cliente;

import java.util.Optional;

public interface ClienteService {
    Cliente crearCliente(RegistroForm form);
    Cliente actualizarDatos (Long idCliente, PerfilForm form);
    Optional<Cliente> buscarPorCorreo(String correo);
}
