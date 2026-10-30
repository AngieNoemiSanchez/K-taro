package com.example.demo.controller.admin;

import com.example.demo.dto.admin.ProductForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Estado;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.model.catalogo.TipoProducto;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.ProductoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    private static final String CREATE_VIEW = "productos/Crearproducto";
    private static final String EDIT_VIEW = "productos/Editarproducto";

    private final ProductoService productoService;
    private final ProductoMapper mapper;
    private final ImageStorageService images;

    public AdminProductoController(ProductoService productoService,
                                   ProductoMapper mapper,
                                   ImageStorageService images) {
        this.productoService = productoService;
        this.mapper = mapper;
        this.images = images;
    }

    @GetMapping
    public String listar() {
        return "redirect:/admin/catalogo?tipo=productos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("productForm", new ProductForm());
        cargarCombos(model);
        return CREATE_VIEW;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("productForm") ProductForm form,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        validarImagen(form, result);
        if (!result.hasErrors()) {
            try {
                Producto creado = productoService.crear(form);
                redirect.addFlashAttribute("mensaje", "Producto registrado correctamente.");
                return "redirect:/admin/productos/" + creado.getIdProducto() + "/editar";
            } catch (BusinessException ex) {
                result.reject("producto.error", ex.getMessage());
            }
        }
        cargarCombos(model);
        return CREATE_VIEW;
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, HttpServletResponse response) {
        Optional<Producto> p = productoService.buscarPorId(id);
        if (p.isEmpty()) {
            return noEncontrado(id, model, response);
        }
        model.addAttribute("productForm", mapper.toForm(p.get()));
        model.addAttribute("productId", id);
        model.addAttribute("currentImage", p.get().getImagen());
        cargarCombos(model);
        return EDIT_VIEW;
    }

    @PostMapping("/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("productForm") ProductForm form,
                             BindingResult result, Model model, HttpServletResponse response,
                             RedirectAttributes redirect) {
        Optional<Producto> actual = productoService.buscarPorId(id);
        if (actual.isEmpty()) {
            return noEncontrado(id, model, response);
        }
        validarImagen(form, result);
        if (!result.hasErrors()) {
            try {
                productoService.actualizar(id, form);
                redirect.addFlashAttribute("mensaje", "Producto actualizado correctamente.");
                return "redirect:/admin/productos/" + id + "/editar";
            } catch (BusinessException ex) {
                result.reject("producto.error", ex.getMessage());
            }
        }
        model.addAttribute("productId", id);
        model.addAttribute("currentImage", actual.get().getImagen());
        cargarCombos(model);
        return EDIT_VIEW;
    }

    @PostMapping("/{id}/stock")
    public String aumentarStock(@PathVariable Long id,
                                @RequestParam(defaultValue = "1") Integer cantidad,
                                RedirectAttributes redirect) {
        if (cantidad == null || cantidad < 1 || cantidad > 100000) {
            redirect.addFlashAttribute("error", "La cantidad a agregar debe ser un número entero mayor a 0.");
        } else {
            try {
                productoService.agregarStock(id, cantidad);
                redirect.addFlashAttribute("mensaje", "Se agregaron " + cantidad + " unidades al stock.");
            } catch (BusinessException ex) {
                redirect.addFlashAttribute("error", ex.getMessage());
            }
        }
        return "redirect:/admin/catalogo?tipo=productos";
    }

    private void cargarCombos(Model model) {
        model.addAttribute("tipos", TipoProducto.values());
        model.addAttribute("estados", Estado.values());
    }

    private void validarImagen(ProductForm form, BindingResult result) {
        images.validate(form.getImage())
                .ifPresent(msg -> result.rejectValue("image", "image.invalid", msg));
    }

    private static String noEncontrado(Long id, Model model, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        model.addAttribute("productId", id);
        return "productos/no-encontrado";
    }
}