package com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.stereotype.Service;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el servicio real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain.StockDomainService}.
 *
 * Mantener este wrapper permite migrar imports por etapas sin romper compilación.
 */
@Deprecated(forRemoval = true)
@Service("legacyStockDomainService")
public class StockDomainService {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain.StockDomainService delegate;

    public StockDomainService(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain.StockDomainService delegate) {
        this.delegate = delegate;
    }

    public MovimientoInventario ingresarStock(Producto producto, Ubicacion ubicacion, int cantidad) {
        return delegate.ingresarStock(producto, ubicacion, cantidad);
    }

    public MovimientoInventario retirarStock(Producto producto, Ubicacion ubicacion, int cantidad) {
        return delegate.retirarStock(producto, ubicacion, cantidad);
    }

    public MovimientoInventario transferirStock(Producto producto, Ubicacion origen, Ubicacion destino, int cantidad) {
        return delegate.transferirStock(producto, origen, destino, cantidad);
    }

    public void retirarStockDistribuido(Producto producto, int cantidadTotal) {
        delegate.retirarStockDistribuido(producto, cantidadTotal);
    }

    public void ingresarStockDistribuido(Producto producto, int cantidadTotal) {
        delegate.ingresarStockDistribuido(producto, cantidadTotal);
    }

    public int calcularStockTotal(Producto producto) {
        return delegate.calcularStockTotal(producto);
    }

    public boolean hayStockSuficiente(Producto producto, int cantidadSolicitada) {
        return delegate.hayStockSuficiente(producto, cantidadSolicitada);
    }
}
