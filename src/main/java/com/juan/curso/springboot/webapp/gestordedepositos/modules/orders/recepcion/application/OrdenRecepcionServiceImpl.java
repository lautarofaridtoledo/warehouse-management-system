package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.application;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.persistence.OrdenRecepcionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.domain.StockDomainService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.application.ProveedorServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Application service del módulo Orders/Recepción.
 *
 * Implementa la lógica (copiada del legacy) para evitar ciclos de beans.
 */
@Service("ordenRecepcionService")
public class OrdenRecepcionServiceImpl implements GenericService<OrdenRecepcion, Long> {

    private final OrdenRecepcionRepositorio ordenRecepcionRepositorio;
    private final ProveedorServiceImpl proveedorService;
    private final ProductoServiceImpl productoService;
    private final StockDomainService stockDomainService;

    public OrdenRecepcionServiceImpl(OrdenRecepcionRepositorio ordenRecepcionRepositorio,
                                     ProveedorServiceImpl proveedorService,
                                     ProductoServiceImpl productoService,
                                     StockDomainService stockDomainService) {
        this.ordenRecepcionRepositorio = ordenRecepcionRepositorio;
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.stockDomainService = stockDomainService;
    }

    @Override
    public Optional<List<OrdenRecepcion>> buscarTodos() {
        return Optional.of(new ArrayList<>(ordenRecepcionRepositorio.findAll()));
    }

    @Override
    public Optional<OrdenRecepcion> buscarPorId(Long id) {
        return ordenRecepcionRepositorio.findById(id);
    }

    @Override
    public OrdenRecepcion crear(OrdenRecepcion ordenRecepcion) {
        return ordenRecepcionRepositorio.save(ordenRecepcion);
    }

    @Override
    public OrdenRecepcion actualizar(OrdenRecepcion ordenRecepcion) {
        return ordenRecepcionRepositorio.saveAndFlush(ordenRecepcion);
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        ordenRecepcionRepositorio.deleteById(id);
    }

    @Transactional
    public OrdenRecepcion procesarEntradaMercaderia(OrdenRecepcionDTO dto) {
        Long proveedorId = dto.getProveedorId();
        if (proveedorId == null || !proveedorService.ExistePorIdProveedor(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado");
        }

        OrdenRecepcion orden = new OrdenRecepcion();
    orden.setProveedorId(proveedorId);
        orden.setFecha(Calendar.getInstance().getTime());
        orden.setEstado(dto.getEstado());

        List<DetalleRecepcion> detalles = new ArrayList<>();

        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            Long productoId = detalleDTO.getProductoId();
            if (productoId == null || !productoService.ExistePorIdProducto(productoId)) {
                throw new RecursoNoEncontradoException("Producto no encontrado");
            }

            detalle.setProductoId(productoId);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(orden);
            detalles.add(detalle);

            stockDomainService.ingresarStockDistribuido(productoId, detalleDTO.getCantidad());
        }

        orden.setDetallesRecepcion(detalles);

        return ordenRecepcionRepositorio.save(orden);
    }

    @Transactional
    public OrdenRecepcion procesarModificacionOrden(Long idOrden, OrdenRecepcionDTO dto) {
        OrdenRecepcion ordenActual = ordenRecepcionRepositorio.findById(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        if (ordenActual.getEstado() == EstadosDeOrden.COMPLETADA) {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }

        for (DetalleRecepcion detalleViejo : ordenActual.getDetallesRecepcion()) {
            stockDomainService.retirarStockDistribuido(detalleViejo.getProductoId(), detalleViejo.getCantidad());
        }

        Long proveedorId = dto.getProveedorId();
        if (proveedorId == null || !proveedorService.ExistePorIdProveedor(proveedorId)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado");
        }
        ordenActual.setProveedorId(proveedorId);
        ordenActual.setFecha(dto.getFecha());
        ordenActual.setEstado(dto.getEstado());

        ordenActual.getDetallesRecepcion().clear();
        ordenRecepcionRepositorio.saveAndFlush(ordenActual);

        List<DetalleRecepcion> nuevosDetalles = new ArrayList<>();
        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            Long productoId = detalleDTO.getProductoId();
            if (productoId == null || !productoService.ExistePorIdProducto(productoId)) {
                throw new RecursoNoEncontradoException("Producto no encontrado");
            }

            detalle.setProductoId(productoId);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(ordenActual);
            nuevosDetalles.add(detalle);

            stockDomainService.ingresarStockDistribuido(productoId, detalleDTO.getCantidad());
        }

        ordenActual.getDetallesRecepcion().addAll(nuevosDetalles);
        return ordenRecepcionRepositorio.save(ordenActual);
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminarConReversion(Long id) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

        for (DetalleRecepcion detalle : orden.getDetallesRecepcion()) {
            stockDomainService.retirarStockDistribuido(detalle.getProductoId(), detalle.getCantidad());
        }

        ordenRecepcionRepositorio.delete(orden);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrderState(Long id, EstadosDeOrden estado) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrado"));

        if (orden.getEstado() == EstadosDeOrden.PENDIENTE && estado == EstadosDeOrden.COMPLETADA) {
            orden.setEstado(estado);
        } else {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }
    }
}
