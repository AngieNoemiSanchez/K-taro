package com.example.demo.service.pedido;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Entity.cliente.ClienteEntity;
import com.example.demo.Entity.pedido.TarjetaEntity;
import com.example.demo.dto.cliente.MetodoDePago;
import com.example.demo.dto.cliente.TarjetaVista;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.TarjetaMapper;
import com.example.demo.model.pedido.EstadoTarjeta;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.TarjetaRepository;

@Service
public class TarjetaService {

    public static final int MAX_TARJETAS = 2;

    private static final ZoneId ZONA_TIENDA = ZoneId.of("America/Lima");
    private static final String DATOS_INVALIDOS = "Los datos de la tarjeta no son válidos";
    private static final String NO_ACEPTADA = "Solo se aceptan tarjetas Visa, Mastercard, American Express o Diners Club";
    private static final String REGISTRADA_INVALIDA = "Selecciona una tarjeta registrada válida";

    private record Tipo(String etiqueta, int longitud, int longitudCvv) {
    }

    private static final Tipo VISA = new Tipo("Visa", 16, 3);
    private static final Tipo MASTERCARD = new Tipo("Mastercard", 16, 3);
    private static final Tipo AMEX = new Tipo("American Express", 15, 4);
    private static final Tipo DINERS = new Tipo("Diners Club", 14, 3);

    private final TarjetaRepository tarjetas;
    private final ClienteRepository clientes;
    private final TarjetaMapper mapper;

    public TarjetaService(TarjetaRepository tarjetas, ClienteRepository clientes, TarjetaMapper mapper) {
        this.tarjetas = tarjetas;
        this.clientes = clientes;
        this.mapper = mapper;
    }

    @Transactional
    public TarjetaEntity vincularParaPago(MetodoDePago datos, ClienteEntity cliente) {
        if (datos == null) {
            throw new BusinessException(DATOS_INVALIDOS);
        }
        if (datos.getTarjetaId() != null) {
            return usarRegistrada(datos.getTarjetaId(), cliente);
        }
        return usarNueva(datos, cliente, true);
    }

    @Transactional
    public void agregar(MetodoDePago datos, String correo) {
        ClienteEntity cliente = buscarCliente(correo);
        usarNueva(datos, cliente, false);
    }

    @Transactional(readOnly = true)
    public List<TarjetaVista> listar(String correo) {
        ClienteEntity cliente = buscarCliente(correo);
        return tarjetas.findByCliente_IdAndEstadoOrderByIdAsc(cliente.getId(), EstadoTarjeta.ACTIVA).stream()
                .map(t -> new TarjetaVista(
                        t.getId(),
                        etiquetaTipo(t.getNumero()),
                        mapper.toModel(t).getNumeroEnmascarado()))
                .toList();
    }

    @Transactional
    public void descartar(Long id, String correo) {
        ClienteEntity cliente = buscarCliente(correo);
        TarjetaEntity tarjeta = tarjetas.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException("Tarjeta no encontrada"));
        if (!esDelCliente(tarjeta, cliente)) {
            throw new BusinessException("Tarjeta no encontrada");
        }
        tarjeta.setEstado(EstadoTarjeta.DESCARTADA);
    }

    public static String etiquetaTipo(String numero) {
        Tipo tipo = detectar(numero);
        return tipo == null ? "Tarjeta" : tipo.etiqueta();
    }

    private TarjetaEntity usarRegistrada(Long id, ClienteEntity cliente) {
        TarjetaEntity tarjeta = tarjetas.findByIdForUpdate(id)
                .orElseThrow(() -> new BusinessException(REGISTRADA_INVALIDA));
        if (!esDelCliente(tarjeta, cliente) || tarjeta.getEstado() != EstadoTarjeta.ACTIVA) {
            throw new BusinessException(REGISTRADA_INVALIDA);
        }
        validarVigencia(tarjeta);
        return tarjeta;
    }

    private TarjetaEntity usarNueva(MetodoDePago datos, ClienteEntity cliente, boolean paraPago) {
        validarFormato(datos);

        TarjetaEntity tarjeta = tarjetas.findByNumeroForUpdate(datos.getNumeroTarjeta())
                .orElseThrow(() -> new BusinessException(DATOS_INVALIDOS));

        if (!tarjeta.getVencimiento().equals(datos.getVencimiento().trim())
                || !tarjeta.getCvv().equals(datos.getCvv())) {
            throw new BusinessException(DATOS_INVALIDOS);
        }

        if (tarjeta.getCliente() != null && !esDelCliente(tarjeta, cliente)) {
            throw new BusinessException("Esta tarjeta ya está asociada a otro cliente");
        }

        validarVigencia(tarjeta);

        boolean activaPropia = esDelCliente(tarjeta, cliente) && tarjeta.getEstado() == EstadoTarjeta.ACTIVA;
        if (activaPropia) {
            if (!paraPago) {
                throw new BusinessException("Esta tarjeta ya está guardada en tu cuenta");
            }
            return tarjeta;
        }

        long activas = tarjetas.countByCliente_IdAndEstado(cliente.getId(), EstadoTarjeta.ACTIVA);
        if (activas >= MAX_TARJETAS) {
            throw new BusinessException("Solo puedes tener " + MAX_TARJETAS
                    + " tarjetas guardadas. Descarta una para usar otra");
        }

        tarjeta.setCliente(cliente);
        if (tarjeta.getTitular() == null || tarjeta.getTitular().isBlank()) {
            tarjeta.setTitular(cliente.getNombre() + " " + cliente.getApellidos());
        }
        tarjeta.setEstado(EstadoTarjeta.ACTIVA);
        return tarjeta;
    }

    private void validarFormato(MetodoDePago datos) {
        if (datos.getNumeroTarjeta() == null || datos.getVencimiento() == null || datos.getCvv() == null
                || datos.getNumeroTarjeta().isBlank() || datos.getVencimiento().isBlank()
                || datos.getCvv().isBlank()) {
            throw new BusinessException("Completa el número, el vencimiento y el CVV de la tarjeta");
        }
        Tipo tipo = detectar(datos.getNumeroTarjeta());
        if (tipo == null) {
            throw new BusinessException(NO_ACEPTADA);
        }
        if (datos.getNumeroTarjeta().length() != tipo.longitud()) {
            throw new BusinessException("Una tarjeta " + tipo.etiqueta() + " debe tener "
                    + tipo.longitud() + " dígitos");
        }
        if (datos.getCvv().length() != tipo.longitudCvv()) {
            throw new BusinessException("El CVV de una tarjeta " + tipo.etiqueta() + " debe tener "
                    + tipo.longitudCvv() + " dígitos");
        }
    }

    private void validarVigencia(TarjetaEntity tarjeta) {
        if (mapper.toModel(tarjeta).estaVencida(LocalDateTime.now(ZONA_TIENDA))) {
            throw new BusinessException("La tarjeta está vencida");
        }
    }

    private boolean esDelCliente(TarjetaEntity tarjeta, ClienteEntity cliente) {
        return tarjeta.getCliente() != null && tarjeta.getCliente().getId().equals(cliente.getId());
    }

    private ClienteEntity buscarCliente(String correo) {
        return clientes.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Cliente no encontrado"));
    }

    private static Tipo detectar(String numero) {
        if (numero == null || !numero.matches("\\d+")) {
            return null;
        }
        if (numero.startsWith("34") || numero.startsWith("37")) {
            return AMEX;
        }
        if (numero.matches("^(30[0-5]|3095|36|38|39).*")) {
            return DINERS;
        }
        if (numero.startsWith("4")) {
            return VISA;
        }
        if (numero.matches("^5[1-5].*")) {
            return MASTERCARD;
        }
        if (numero.length() >= 4) {
            int prefijo = Integer.parseInt(numero.substring(0, 4));
            if (prefijo >= 2221 && prefijo <= 2720) {
                return MASTERCARD;
            }
        }
        return null;
    }
}