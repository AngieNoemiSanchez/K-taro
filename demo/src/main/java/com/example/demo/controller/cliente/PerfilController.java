package com.example.demo.controller.cliente;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.config.CustomUserDetailsService;
import com.example.demo.dto.cliente.PerfilForm;
import com.example.demo.dto.cliente.TarjetaVista;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ClienteMapper;
import com.example.demo.model.cliente.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.Cliente.ClienteService;
import com.example.demo.service.pedido.TarjetaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cliente/perfil")
public class PerfilController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final CustomUserDetailsService userDetailsService;
    private final TarjetaService tarjetaService;

    public PerfilController(ClienteService clienteService, ClienteRepository clienteRepository,
                            ClienteMapper clienteMapper, CustomUserDetailsService userDetailsService,
                            TarjetaService tarjetaService) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.userDetailsService = userDetailsService;
        this.tarjetaService = tarjetaService;
    }

    @ModelAttribute("tarjetas")
    public List<TarjetaVista> tarjetas(Principal principal) {
        return tarjetaService.listar(principal.getName());
    }

    @ModelAttribute("maxTarjetas")
    public int maxTarjetas() {
        return TarjetaService.MAX_TARJETAS;
    }

    @GetMapping
    public String verPerfil(Principal principal, Model model) {
        ClienteEntity entity = clienteRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        model.addAttribute("cliente", clienteMapper.toModel(entity));
        model.addAttribute("perfilForm", aForm(entity));
        return "cliente/perfil";
    }

    @PostMapping
    public String guardarPerfil(@Valid @ModelAttribute("perfilForm") PerfilForm form,
                                BindingResult result,
                                Principal principal,
                                Model model,
                                RedirectAttributes redirect,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        ClienteEntity entity = clienteRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));

        if (!result.hasErrors()) {
            try {
                Cliente actualizado = clienteService.actualizarDatos(entity.getId(), form);
                if (!actualizado.getCorreo().equals(principal.getName())) {
                    refrescarSesion(actualizado.getCorreo(), request, response);
                }
                redirect.addFlashAttribute("success", "¡Perfil actualizado correctamente!");
                return "redirect:/cliente/perfil";
            } catch (BusinessException ex) {
                result.rejectValue("correo", "perfil.error", ex.getMessage());
            }
        }
        model.addAttribute("cliente", clienteMapper.toModel(entity));
        return "cliente/perfil";
    }

    private void refrescarSesion(String correo, HttpServletRequest request, HttpServletResponse response) {
        UserDetails user = userDetailsService.loadUserByUsername(correo);
        UsernamePasswordAuthenticationToken auth =
                UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
    }

    private PerfilForm aForm(ClienteEntity c) {
        PerfilForm f = new PerfilForm();
        f.setCorreo(c.getCorreo());
        f.setTelefono(c.getTelefono());
        f.setDireccion(c.getDireccion());
        return f;
    }
}