package com.example.demo.service.Cliente;

import com.example.demo.dto.cliente.PerfilForm;
import com.example.demo.dto.cliente.RegistroForm;
import com.example.demo.model.cliente.Cliente;

import java.util.Optional;

public interface ClienteService {
    Cliente crearCliente(RegistroForm form);
    Cliente actualizarDatos (Long idCliente, PerfilForm form);
    Optional<Cliente> buscarPorCorreo(String correo);
}
