package com.example.demo.controller.admin;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.admin.PromotionForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.PromocionMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.Promocion;
import com.example.demo.model.catalogo.Tiempo;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.ProductoService;
import com.example.demo.service.catalogo.PromocionService;

@Controller
@RequestMapping("/admin/promociones")
public class AdminPromotionController {

    private static final String CREATE_VIEW = "catalogo/Crearpromocion";
    private static final String EDIT_VIEW = "catalogo/Editarpromocion";
    private static final String NOT_FOUND_VIEW = "catalogo/no-encontrado";

    private final PromocionService promocionService;
    private final ProductoService productoService;
    private final PromocionMapper mapper;
    private final ImageStorageService images;

    public AdminPromotionController(PromocionService promocionService, ProductoService productoService,
                                    PromocionMapper mapper, ImageStorageService images) {
        this.promocionService = promocionService;
        this.productoService = productoService;
        this.mapper = mapper;
        this.images = images;
    }

    @GetMapping
    public String list() {
        return "redirect:/admin/catalogo?tipo=promociones";
    }

    @GetMapping("/nueva")
    public String showCreateForm(Model model) {
        return prepareView(model, new PromotionForm(), CREATE_VIEW);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("promotionForm") PromotionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        validateImage(form, result);
        if (!result.hasErrors()) {
            try {
                promocionService.crear(form);
                redirect.addFlashAttribute("mensaje", "Promoción registrada correctamente.");
                return "redirect:/admin/catalogo?tipo=promociones";
            } catch (BusinessException ex) {
                result.reject("promocion.error", ex.getMessage());
            }
        }
        return prepareView(model, form, CREATE_VIEW);
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<Promocion> promotion = promocionService.buscarPorId(id);
        if (promotion.isEmpty()) {
            return notFound(id, model, response);
        }
        model.addAttribute("promotionId", id);
        model.addAttribute("currentImage", promotion.get().getImagen());
        return prepareView(model, mapper.toForm(promotion.get()), EDIT_VIEW);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("promotionForm") PromotionForm form,
                         BindingResult result, Model model, HttpServletResponse response,
                         RedirectAttributes redirect) {
        Optional<Promocion> current = promocionService.buscarPorId(id);
        if (current.isEmpty()) {
            return notFound(id, model, response);
        }
        validateImage(form, result);
        if (!result.hasErrors()) {
            try {
                promocionService.actualizar(id, form);
                redirect.addFlashAttribute("mensaje", "Promoción actualizada correctamente.");
                return "redirect:/admin/promociones/" + id + "/editar";
            } catch (BusinessException ex) {
                result.reject("promocion.error", ex.getMessage());
            }
        }
        model.addAttribute("promotionId", id);
        model.addAttribute("currentImage", current.get().getImagen());
        return prepareView(model, form, EDIT_VIEW);
    }

    private void validateImage(PromotionForm form, BindingResult result) {
        images.validate(form.getImage())
                .ifPresent(message -> result.rejectValue("image", "image.invalid", message));
    }

    private static String notFound(Long id, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("productId", id);
        return NOT_FOUND_VIEW;
    }

    private String prepareView(Model model, PromotionForm form, String view) {
        model.addAttribute("promotionForm", form);
        model.addAttribute("availableProducts", productoService.listar().stream()
                .filter(p -> p.getEstado() == Estado.ACTIVO).toList());
        model.addAttribute("selectedProducts", selectedProducts(form));
        model.addAttribute("tiempos", Tiempo.values());
        model.addAttribute("tiposPromocion", com.example.demo.model.catalogo.TipoPromocion.values());
        model.addAttribute("estados", Estado.values());
        return view;
    }

    private List<Producto> selectedProducts(PromotionForm form) {
        return form.getProductIds().stream()
                .distinct()
                .map(productoService::buscarPorId)
                .flatMap(Optional::stream)
                .toList();
    }
}