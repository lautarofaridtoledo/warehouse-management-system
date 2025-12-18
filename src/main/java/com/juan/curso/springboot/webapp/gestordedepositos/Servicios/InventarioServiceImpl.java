package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el servicio real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl}.
 *
 * Mantener este wrapper permite migrar imports por etapas sin romper compilación.
 */
@Deprecated(forRemoval = true)
@Service("legacyInventarioService")
public class InventarioServiceImpl implements GenericService<Inventario, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl delegate;

    public InventarioServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Inventario>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) throws RecursoNoEncontradoException {
        return delegate.buscarPorId(id);
    }

    @Override
    public Inventario crear(Inventario inventario) {
        return delegate.crear(inventario);
    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        return delegate.actualizar(inventario);
    }

    @Override
    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    public List<Inventario> buscarInventariosPorIdProducto(Long idProducto) throws RecursoNoEncontradoException{
        return delegate.buscarInventariosPorIdProducto(idProducto);

    }

    public List<Inventario> buscarPorCodigoSku(String codigoSku) {
        return delegate.buscarPorCodigoSku(codigoSku);
    }

    public int calcularStockTotalPorIdProducto(Long idProducto) {
        return delegate.calcularStockTotalPorIdProducto(idProducto);
    }

    public int calcularStockTotalPorCodigoSku(String codigoSku) {
        return delegate.calcularStockTotalPorCodigoSku(codigoSku);
    }

    public List<Inventario> disminuirCantidad(DetalleDespacho detalleDespacho) {
        return delegate.disminuirCantidad(detalleDespacho);
    }

    public void deshacerIngreso(Producto producto, int cantidad) {
        delegate.deshacerIngreso(producto, cantidad);
    }

    /**
     * Agrega mercadería distribuyendo en ubicaciones disponibles.
     * Delega completamente a StockDomainService.
     */
    public void agregarMercaderiaConProductoPersistido(Producto producto, int cantidad) {
        delegate.agregarMercaderiaConProductoPersistido(producto, cantidad);
    }
}