package com.example.demo.controller;

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

import com.example.demo.dto.ProductForm;
import com.example.demo.model.AdminProduct;
import com.example.demo.service.ProductAdminService;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductController {

    private static final String CREATE_VIEW = "/crearProducto";
    private static final String EDIT_VIEW = "/editarProducto";

    private final ProductAdminService productAdmin;

    public AdminProductController(ProductAdminService productAdmin) {
        this.productAdmin = productAdmin;
    }



    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return CREATE_VIEW;
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("productForm") ProductForm form, BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return CREATE_VIEW;
        }
        AdminProduct created = productAdmin.create(form);
        redirect.addFlashAttribute("mensaje", "Producto registrado correctamente.");
        return "redirect:/admin/productos/" + created.id() + "/editar";
    }



    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable String id, Model model, HttpServletResponse response) {
        Optional<AdminProduct> product = productAdmin.findById(id);
        if (product.isEmpty()) {
            return notFound(id, model, response);
        }
        model.addAttribute("productForm", toForm(product.get()));
        model.addAttribute("productId", id);
        return EDIT_VIEW;
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("productForm") ProductForm form,
                         BindingResult result, Model model, HttpServletResponse response, RedirectAttributes redirect) {
        if (productAdmin.findById(id).isEmpty()) {
            return notFound(id, model, response);
        }
        if (result.hasErrors()) {
            model.addAttribute("productId", id);
            return EDIT_VIEW;
        }
        productAdmin.update(id, form);
        redirect.addFlashAttribute("mensaje", "Producto actualizado correctamente.");
        return "redirect:/admin/productos/" + id + "/editar";
    }

    private static String notFound(String id, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("productId", id);
        return "productos/no-encontrado";
    }

    private static ProductForm toForm(AdminProduct product) {
        ProductForm form = new ProductForm();
        form.setName(product.name());
        form.setCategory(product.category());
        form.setDescription(product.description());
        form.setPrice(product.price());
        form.setStock(product.stock());
        form.setActive(product.active());
        return form;
    }
}