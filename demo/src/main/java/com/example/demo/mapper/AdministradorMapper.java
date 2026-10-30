package com.example.demo.mapper;

import com.example.demo.Entity.admin.AdministradorEntity;
import com.example.demo.Entity.admin.HorarioAdminEntity;
import com.example.demo.model.admin.Administrador;
import com.example.demo.model.admin.HorarioAdmin;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AdministradorMapper {

    public Administrador toModel(AdministradorEntity e) {
        if (e == null) return null;
        return new Administrador(
                e.getId(),
                e.getNombre(),
                e.getApellido(),
                e.getCorreo(),
                null,
                e.getFechaNacimiento(),
                e.getTelefono(),
                e.getDni(),
                toModel(e.getHorarioAdmin())
        );
    }

    public HorarioAdmin toModel(HorarioAdminEntity h) {
        if (h == null) return null;
        return new HorarioAdmin(h.getId(), h.getDias(), h.getHoraInicio(), h.getHoraFin());
    }


    public UserDetails toUserDetails(AdministradorEntity e) {
        return User.builder()
                .username(e.getCorreo())
                .password(e.getPassword())
                .roles("ADMIN")
                .build();
    }
}