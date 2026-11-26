package com.example.demo.service.catalogo;

import com.example.demo.Entity.catalogo.ProductoEntity;
import com.example.demo.dto.admin.ProductForm;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.ProductoMapper;
import com.example.demo.model.catalogo.Producto;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.ImageStorageService;
import com.example.demo.service.catalogo.impl.ProductoServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceImplTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void crearRejectsInvalidFormBeforePersisting() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ImageStorageService images = mock(ImageStorageService.class);
        ProductoServiceImpl service = new ProductoServiceImpl(
                repository, new ProductoMapper(), images, validator);

        assertThrows(BusinessException.class, () -> service.crear(new ProductForm()));

        verifyNoInteractions(repository, images);
    }

    @Test
    void agregarStockRejectsTotalsAboveMaximum() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = new ProductoEntity();
        product.setStock(Producto.MAX_STOCK - 1);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        ProductoServiceImpl service = new ProductoServiceImpl(
                repository, new ProductoMapper(), mock(ImageStorageService.class), validator);

        assertThrows(BusinessException.class, () -> service.agregarStock(1L, 2));

        verify(repository, never()).save(any(ProductoEntity.class));
    }

    @Test
    void agregarStockRejectsNonPositiveAndNegativeOrInvalidCurrentStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoServiceImpl service = new ProductoServiceImpl(
                repository, new ProductoMapper(), mock(ImageStorageService.class), validator);

        assertThrows(BusinessException.class, () -> service.agregarStock(1L, 0));
        assertThrows(BusinessException.class, () -> service.agregarStock(1L, -1));

        ProductoEntity negative = new ProductoEntity();
        negative.setStock(-1);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(negative));
        assertThrows(BusinessException.class, () -> service.agregarStock(1L, 1));

        ProductoEntity nullStock = new ProductoEntity();
        nullStock.setStock(null);
        when(repository.findByIdForUpdate(3L)).thenReturn(Optional.of(nullStock));
        assertThrows(BusinessException.class, () -> service.agregarStock(3L, 1));

        ProductoEntity aboveMaximum = new ProductoEntity();
        aboveMaximum.setStock(Producto.MAX_STOCK + 1);
        when(repository.findByIdForUpdate(2L)).thenReturn(Optional.of(aboveMaximum));
        assertThrows(BusinessException.class, () -> service.agregarStock(2L, 1));

        verify(repository, never()).save(any(ProductoEntity.class));
    }

    @Test
    void agregarStockUsesPessimisticLockAndReachesMaximum() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = new ProductoEntity();
        product.setStock(98);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);
        ProductoServiceImpl service = new ProductoServiceImpl(
                repository, new ProductoMapper(), mock(ImageStorageService.class), validator);

        service.agregarStock(1L, 2);

        verify(repository).findByIdForUpdate(1L);
        verify(repository).save(product);
        org.junit.jupiter.api.Assertions.assertEquals(Producto.MAX_STOCK, product.getStock());
    }

    @Test
    void retirarStockUsesPessimisticLockAndAllowsZero() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(2);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(product);

        createService(repository).retirarStock(1L, 2);

        verify(repository).findByIdForUpdate(1L);
        assertEquals(0, product.getStock());
    }

    @Test
    void retirarStockRejectsInvalidAmountsAndNegativeResult() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(2);
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        ProductoServiceImpl service = createService(repository);

        assertThrows(BusinessException.class, () -> service.retirarStock(1L, 0));
        assertThrows(BusinessException.class, () -> service.retirarStock(1L, -1));
        assertThrows(BusinessException.class, () -> service.retirarStock(1L, 3));

        verify(repository, never()).save(any(ProductoEntity.class));
    }

    @Test
    void actualizarNombreDoesNotChangeStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(30);
        stubUpdate(repository, product, "Producto editado");
        ProductForm form = validForm();
        form.setName("Producto editado");

        createService(repository).actualizar(1L, form);

        verify(repository).findByIdForUpdate(1L);
        assertEquals(30, product.getStock());
        assertEquals("Producto editado", product.getNombre());
    }

    @Test
    void actualizarPrecioDoesNotChangeStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(30);
        stubUpdate(repository, product, "Producto");
        ProductForm form = validForm();
        form.setPrice(BigDecimal.valueOf(12));

        createService(repository).actualizar(1L, form);

        assertEquals(30, product.getStock());
        assertEquals(BigDecimal.valueOf(12), product.getPrecio());
    }

    @Test
    void actualizarIgnoresManipulatedFormStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(30);
        stubUpdate(repository, product, "Producto");
        ProductForm form = validForm();
        form.setStock(99);

        createService(repository).actualizar(1L, form);

        assertEquals(30, product.getStock());
    }

    @Test
    void staleEditAfterPurchasePreservesPurchasedStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(29);
        stubUpdate(repository, product, "Producto");
        ProductForm staleForm = validForm();
        staleForm.setStock(30);

        createService(repository).actualizar(1L, staleForm);

        assertEquals(29, product.getStock());
    }

    @Test
    void staleEditAfterStockIncrementPreservesIncrementedStock() {
        ProductoRepository repository = mock(ProductoRepository.class);
        ProductoEntity product = productWithStock(31);
        stubUpdate(repository, product, "Producto");
        ProductForm staleForm = validForm();
        staleForm.setStock(30);

        createService(repository).actualizar(1L, staleForm);

        assertEquals(31, product.getStock());
    }

    private static ProductoServiceImpl createService(ProductoRepository repository) {
        return new ProductoServiceImpl(
                repository, new ProductoMapper(), mock(ImageStorageService.class), validator);
    }

    private static ProductoEntity productWithStock(int stock) {
        ProductoEntity product = new ProductoEntity();
        product.setStock(stock);
        product.setNombre("Producto");
        product.setDescripcion("Descripcion");
        product.setPrecio(BigDecimal.valueOf(10));
        product.setTipoProducto(com.example.demo.model.catalogo.TipoProducto.SNACK);
        product.setEstado(com.example.demo.model.catalogo.Estado.ACTIVO);
        return product;
    }

    private static void stubUpdate(ProductoRepository repository, ProductoEntity product, String name) {
        when(repository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(repository.existsByNombreIgnoreCaseAndIdNot(name, 1L)).thenReturn(false);
        when(repository.save(product)).thenReturn(product);
    }

    private static ProductForm validForm() {
        ProductForm form = new ProductForm();
        form.setName("Producto");
        form.setDescription("Descripcion");
        form.setPrice(BigDecimal.valueOf(10));
        form.setStock(30);
        form.setTipoProducto(com.example.demo.model.catalogo.TipoProducto.SNACK);
        form.setEstado(com.example.demo.model.catalogo.Estado.ACTIVO);
        return form;
    }
}
