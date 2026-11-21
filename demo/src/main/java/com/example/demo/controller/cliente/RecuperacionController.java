package com.example.demo.controller.cliente;

import com.example.demo.dto.cliente.RestablecerForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.Cliente.RecuperacionService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente")
public class RecuperacionController {

    private final RecuperacionService recuperacionService;

    public RecuperacionController(RecuperacionService recuperacionService) {
        this.recuperacionService = recuperacionService;
    }

    @GetMapping("/olvide")
    public String mostrarOlvide() {
        return "cliente/olvide";
    }

    @PostMapping("/olvide")
    public String procesarOlvide(@RequestParam("correo") String correo,
                                 Model model,
                                 RedirectAttributes redirect) {
        try {
            recuperacionService.solicitar(correo);
        } catch (BusinessException ex) {
            model.addAttribute("error", ex.getMessage());
            return "cliente/olvide";
        }
        redirect.addFlashAttribute("success",
                "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña.");
        return "redirect:/cliente/login";
    }

    @GetMapping("/restablecer")
    public String mostrarRestablecer(@RequestParam("token") String token, Model model) {
        if (!recuperacionService.tokenValido(token)) {
            model.addAttribute("tokenInvalido", true);
            return "cliente/restablecer";
        }
        RestablecerForm form = new RestablecerForm();
        form.setToken(token);
        model.addAttribute("restablecerForm", form);
        return "cliente/restablecer";
    }

    @PostMapping("/restablecer")
    public String procesarRestablecer(@Valid @ModelAttribute("restablecerForm") RestablecerForm form,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirect) {
        if (!result.hasFieldErrors("password") && !result.hasFieldErrors("confirmarPassword")
                && !form.getPassword().equals(form.getConfirmarPassword())) {
            result.rejectValue("confirmarPassword", "restablecer.mismatch", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            return "cliente/restablecer";
        }
        try {
            recuperacionService.restablecer(form.getToken(), form.getPassword());
        } catch (BusinessException ex) {
            model.addAttribute("tokenInvalido", true);
            return "cliente/restablecer";
        }
        redirect.addFlashAttribute("success", "Contraseña actualizada. Ya puedes iniciar sesión.");
        return "redirect:/cliente/login";
    }
}