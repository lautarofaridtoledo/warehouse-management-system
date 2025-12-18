package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el servicio real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl}.
 */
@Deprecated(forRemoval = true)
@Service("legacyProductoService")
public class ProductoServiceImpl implements GenericService<Producto, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl delegate;

    @Autowired
    public ProductoServiceImpl(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Producto>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return delegate.buscarPorId(id);
    }

    @Override
    public Producto crear(Producto producto) {
        return delegate.crear(producto);
    }

    @Override
    public Producto actualizar(Producto productoEditado) {
        return delegate.actualizar(productoEditado);
    }

    public void eliminar(Long id) {
        delegate.eliminar(id);
    }

    public Producto buscarPorCodigoSKU(String codigo) {
        return delegate.buscarPorCodigoSKU(codigo);
    }
}
