package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventory.movimientos.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.GenericService;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain.StockDomainService;
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

        Producto producto = productoRepositorio.findById(dto.getProducto().getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Ubicacion origen = ubicacionRepositorio.findById(dto.getUbicacionOrigen().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación origen no encontrada"));

        Ubicacion destino = ubicacionRepositorio.findById(dto.getUbicacionDestino().getIdUbicacion())
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación destino no encontrada"));

        return stockDomainService.transferirStock(producto, origen, destino, dto.getCantidad());
    }

    /**
     * Revierte un movimiento y lo elimina.
     */
    @Transactional(rollbackFor = Exception.class)
    public void revertirYEliminar(Long idMovimiento) {
        MovimientoInventario movimiento = buscarPorId(idMovimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado"));

        stockDomainService.transferirStock(
                movimiento.getProducto(),
                movimiento.getUbicacionDestino(),
                movimiento.getUbicacionOrigen(),
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
