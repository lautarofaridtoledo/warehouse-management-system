package com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.domain;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;

/**
 * Contrato de dominio para operaciones de stock.
 *
 * Está en shared porque lo consumen múltiples módulos (orders/**, inventario/**).
 * La implementación actual vive en inventario:
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain.StockDomainService}.
 */
public interface StockDomainService {

    MovimientoInventario ingresarStock(Producto producto, Ubicacion ubicacion, int cantidad);

    MovimientoInventario retirarStock(Producto producto, Ubicacion ubicacion, int cantidad);

    MovimientoInventario transferirStock(Producto producto, Ubicacion origen, Ubicacion destino, int cantidad);

    /**
     * Variante boundary-safe: opera usando IDs (sin relaciones JPA cross-module).
     */
    MovimientoInventario transferirStock(Long productoId, Long ubicacionOrigenId, Long ubicacionDestinoId, int cantidad);

    void retirarStockDistribuido(Producto producto, int cantidadTotal);

    void retirarStockDistribuido(Long productoId, int cantidadTotal);

    void ingresarStockDistribuido(Producto producto, int cantidadTotal);

    void ingresarStockDistribuido(Long productoId, int cantidadTotal);

    int calcularStockTotal(Producto producto);

    int calcularStockTotal(Long productoId);

    boolean hayStockSuficiente(Producto producto, int cantidadSolicitada);

    boolean hayStockSuficiente(Long productoId, int cantidadSolicitada);
}
