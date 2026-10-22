package com.example.demo.controller;

import java.math.BigDecimal;
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

import com.example.demo.dto.PromotionForm;
import com.example.demo.model.AdminProduct;
import com.example.demo.model.Promotion;
import com.example.demo.service.ProductAdminService;
import com.example.demo.service.PromotionService;

@Controller
@RequestMapping("/admin/promociones")
public class AdminPromotionController {

    private static final String CREATE_VIEW = "productos/Crearpromocion";
    private static final String EDIT_VIEW = "productos/Editarpromocion";

    private final PromotionService promotions;
    private final ProductAdminService productAdmin;

    public AdminPromotionController(PromotionService promotions, ProductAdminService productAdmin) {
        this.promotions = promotions;
        this.productAdmin = productAdmin;
    }

    @GetMapping
    public String list() {
        return "redirect:/admin/catalogo?tipo=promociones";
    }

    @GetMapping("/nueva")
    public String showCreateForm(Model model) {
        return prepareView(model, new PromotionForm());
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("promotionForm") PromotionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        validateContent(form, result);
        if (result.hasErrors()) {
            return prepareView(model, form);
        }
        promotions.create(form);
        redirect.addFlashAttribute("mensaje", "Promoción registrada correctamente.");
        return "redirect:/admin/catalogo?tipo=promociones";
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable String id, Model model, HttpServletResponse response) {
        Optional<Promotion> promotion = promotions.findById(id);
        if (promotion.isEmpty()) {
            return notFound(id, model, response);
        }
        model.addAttribute("promotionId", id);
        return prepareView(model, toForm(promotion.get()), EDIT_VIEW);
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("promotionForm") PromotionForm form,
                         BindingResult result, Model model, HttpServletResponse response,
                         RedirectAttributes redirect) {
        if (promotions.findById(id).isEmpty()) {
            return notFound(id, model, response);
        }
        validateContent(form, result);
        if (result.hasErrors()) {
            model.addAttribute("promotionId", id);
            return prepareView(model, form, EDIT_VIEW);
        }
        promotions.update(id, form);
        redirect.addFlashAttribute("mensaje", "Promoción actualizada correctamente.");
        return "redirect:/admin/promociones/" + id + "/editar";
    }

    private void validateContent(PromotionForm form, BindingResult result) {
        List<AdminProduct> selected = selectedProducts(form);
        if (selected.size() != form.getProductIds().stream().distinct().count()) {
            result.rejectValue("productIds", "productIds.invalid", "Alguno de los productos no existe");
        } else if (!result.hasFieldErrors("price") && !selected.isEmpty()) {
            BigDecimal regularTotal = selected.stream().map(AdminProduct::price)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (form.getPrice().compareTo(regularTotal) >= 0) {
                result.rejectValue("price", "price.notLower",
                        "El precio de la promoción debe ser menor a S/ " + regularTotal);
            }
        }
    }

    private static String notFound(String id, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("productId", id);
        return "productos/no-encontrado";
    }

    private static PromotionForm toForm(Promotion promotion) {
        PromotionForm form = new PromotionForm();
        form.setName(promotion.name());
        form.setDuration(promotion.duration());
        form.setDescription(promotion.description());
        form.setProductIds(new java.util.ArrayList<>(promotion.productIds()));
        form.setPrice(promotion.price());
        form.setActive(promotion.active());
        return form;
    }

    private String prepareView(Model model, PromotionForm form) {
        return prepareView(model, form, CREATE_VIEW);
    }

    private String prepareView(Model model, PromotionForm form, String view) {
        model.addAttribute("promotionForm", form);
        model.addAttribute("availableProducts", productAdmin.findAll().stream().filter(AdminProduct::active).toList());
        model.addAttribute("selectedProducts", selectedProducts(form));
        return view;
    }

    private List<AdminProduct> selectedProducts(PromotionForm form) {
        return form.getProductIds().stream()
                .distinct()
                .map(productAdmin::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }
}