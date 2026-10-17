package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.PromotionForm;
import com.example.demo.model.AdminProduct;
import com.example.demo.service.ProductAdminService;
import com.example.demo.service.PromotionService;

@Controller
@RequestMapping("/admin/promociones")
public class AdminPromotionController {

    private static final String CREATE_VIEW = "/crearPromocion";

    private final PromotionService promotions;
    private final ProductAdminService productAdmin;

    public AdminPromotionController(PromotionService promotions, ProductAdminService productAdmin) {
        this.promotions = promotions;
        this.productAdmin = productAdmin;
    }

    @GetMapping("/nueva")
    public String showCreateForm(Model model) {
        return prepareView(model, new PromotionForm());
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("promotionForm") PromotionForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
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

        if (result.hasErrors()) {
            return prepareView(model, form);
        }
        promotions.create(form);
        redirect.addFlashAttribute("mensaje", "Promoción registrada correctamente.");
        return "redirect:/admin/promociones/nueva";
    }

    private String prepareView(Model model, PromotionForm form) {
        model.addAttribute("promotionForm", form);
        model.addAttribute("availableProducts", productAdmin.findAll().stream().filter(AdminProduct::active).toList());
        model.addAttribute("selectedProducts", selectedProducts(form));
        return CREATE_VIEW;
    }

    private List<AdminProduct> selectedProducts(PromotionForm form) {
        return form.getProductIds().stream()
                .distinct()
                .map(productAdmin::findById)
                .flatMap(java.util.Optional::stream)
                .toList();
    }
}