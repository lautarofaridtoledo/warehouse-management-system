package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.application;

import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.persistence.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.persistence.UbicacionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.persistence.ProductoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.domain.StockDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service para Movimientos de Inventario.
 *
 * Implementación real (copiada del legacy) para evitar ciclos de beans.
 */
@Service("movimientoInventarioService")
public class MovimientoInventarioServiceImpl implements GenericService<MovimientoInventario, Long> {

    private final MovimientoInventarioRepositorio movimientoRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final StockDomainService stockDomainService;

    public MovimientoInventarioServiceImpl(MovimientoInventarioRepositorio movimientoRepositorio,
                                          UbicacionRepositorio ubicacionRepositorio,
                                          ProductoRepositorio productoRepositorio,
                                          StockDomainService stockDomainService) {
        this.movimientoRepositorio = movimientoRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.stockDomainService = stockDomainService;
    }

    /**
     * Procesa un movimiento de reubicación delegando a StockDomainService.
     */
    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario procesarMovimiento(MovimientoInventarioDTO dto) {
        if (dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

    if (dto.getProductoId() == null || dto.getUbicacionOrigenId() == null || dto.getUbicacionDestinoId() == null) {
        throw new IllegalArgumentException("El movimiento requiere productoId, ubicacionOrigenId y ubicacionDestinoId");
    }

    // validación de existencia (evita transferencias a IDs inexistentes)
    productoRepositorio.findById(dto.getProductoId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
    ubicacionRepositorio.findById(dto.getUbicacionOrigenId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));
    ubicacionRepositorio.findById(dto.getUbicacionDestinoId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación destino no encontrada"));

    return stockDomainService.transferirStock(dto.getProductoId(), dto.getUbicacionOrigenId(), dto.getUbicacionDestinoId(), dto.getCantidad());
    }

    /**
     * Revierte un movimiento y lo elimina.
     */
    @Transactional(rollbackFor = Exception.class)
    public void revertirYEliminar(Long idMovimiento) {
        MovimientoInventario movimiento = buscarPorId(idMovimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado"));

    stockDomainService.transferirStock(
        movimiento.getProductoId(),
        movimiento.getUbicacionDestinoId(),
        movimiento.getUbicacionOrigenId(),
        movimiento.getCantidad()
    );

        movimientoRepositorio.delete(movimiento);
    }

    @Override
    public Optional<List<MovimientoInventario>> buscarTodos() {
        return Optional.of(movimientoRepositorio.findAll());
    }

    @Override
    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return movimientoRepositorio.findById(id);
    }

    @Override
    public MovimientoInventario crear(MovimientoInventario entity) {
        return movimientoRepositorio.save(entity);
    }

    @Override
    public MovimientoInventario actualizar(MovimientoInventario entity) {
        return movimientoRepositorio.save(entity);
    }

    @Override
    public void eliminar(Long id) {
        movimientoRepositorio.deleteById(id);
    }
}
