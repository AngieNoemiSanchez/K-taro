package com.example.demo.controller.cliente;

import com.example.demo.dto.cliente.RegistroForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.Cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping ("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }


    @GetMapping("/login")
    public String mostrarLogin() {
        return "cliente/loginCliente";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroForm", new RegistroForm());
        return "cliente/registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroForm") RegistroForm form,
                                   BindingResult result,
                                   RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "cliente/registro";
        }
        try {
            clienteService.crearCliente(form);
        } catch (BusinessException ex) {
            result.reject("registro.error", ex.getMessage());
            return "cliente/registro";
        }
        redirect.addFlashAttribute("success", "¡Cuenta creada exitosamente! Ahora puedes iniciar sesión.");
        return "redirect:/cliente/login";
    }
}
