package com.example.demo.mapper;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.dto.PerfilForm;
import com.example.demo.dto.RegistroForm;
import com.example.demo.model.cliente.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    public ClienteEntity toEntity(RegistroForm form) {
        if (form == null) return null;
        ClienteEntity entity = new ClienteEntity();
        entity.setNombre(form.getNombre());
        entity.setApellidos(form.getApellidos());
        entity.setTelefono(form.getTelefono());
        entity.setDireccion(form.getDireccion());
        entity.setCorreo(form.getCorreo());
        entity.setPassword(form.getPassword());
        entity.setDni(form.getDni());
        entity.setFechaNacimiento(form.getFechaNacimiento());
        return entity;
    }

    public Cliente toModel (ClienteEntity entity){
        if (entity ==null) return null;
        return new Cliente(
                entity.getId(),
                entity.getNombre(),
                entity.getApellidos(),
                entity.getTelefono(),
                entity.getDireccion(),
                entity.getCorreo(),
                null,
                entity.getFechaNacimiento(),
                entity.getDni()
        );
    }

    public void updateEntityFromForm(PerfilForm form, ClienteEntity entity) {
        entity.setNombre(form.getNombre());
        entity.setApellidos(form.getApellidos());
        entity.setTelefono(form.getTelefono());
        entity.setDireccion(form.getDireccion());
    }
}
