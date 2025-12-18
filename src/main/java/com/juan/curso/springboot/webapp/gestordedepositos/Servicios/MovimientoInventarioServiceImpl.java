package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.MovimientoInventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain.StockDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper legacy para mantener compatibilidad con inyecciones por tipo.
 *
 * La implementación real vive en modules/inventory/movimientos/application.
 */
@Deprecated
@Service("legacyMovimientoInventarioService")
public class MovimientoInventarioServiceImpl implements GenericService<MovimientoInventario, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.inventory.movimientos.application.MovimientoInventarioServiceImpl delegate;

    @Autowired
    public MovimientoInventarioServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.inventory.movimientos.application.MovimientoInventarioServiceImpl delegate) {
        this.delegate = delegate;
    }

    /**
     * Procesa un movimiento de reubicación delegando a StockDomainService.
     * El domain service se encarga de: validar stock, actualizar inventarios,
     * actualizar ubicaciones y registrar el movimiento.
     */
    @Transactional(rollbackFor = Exception.class)
    public MovimientoInventario procesarMovimiento(MovimientoInventarioDTO dto) {
    return delegate.procesarMovimiento(dto);
    }

    /**
     * Revierte un movimiento y lo elimina.
     * Utiliza StockDomainService para hacer la transferencia inversa.
     */
    @Transactional(rollbackFor = Exception.class)
    public void revertirYEliminar(Long idMovimiento) {
    delegate.revertirYEliminar(idMovimiento);
    }

    @Override
    public Optional<List<MovimientoInventario>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<MovimientoInventario> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public MovimientoInventario crear(MovimientoInventario entity) {
        return delegate.crear(entity);
    }

    @Override
    public MovimientoInventario actualizar(MovimientoInventario entity) {
        return delegate.actualizar(entity);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }
}