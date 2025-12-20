package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.persistence.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.persistence.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.persistence.UbicacionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application.ProductoServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio que centraliza todas las operaciones de stock.
 * Garantiza consistencia entre Inventario, Ubicacion y MovimientoInventario.
 */
@Service
public class StockDomainService implements com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.domain.StockDomainService {

    private final InventarioRepositorio inventarioRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final MovimientoInventarioRepositorio movimientoRepositorio;
    private final ProductoServiceImpl productoService;

    public StockDomainService(InventarioRepositorio inventarioRepositorio,
                              UbicacionRepositorio ubicacionRepositorio,
                              MovimientoInventarioRepositorio movimientoRepositorio,
                              ProductoServiceImpl productoService) {
        this.inventarioRepositorio = inventarioRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.movimientoRepositorio = movimientoRepositorio;
        this.productoService = productoService;
    }

    @Override
    @Transactional
    public void retirarStockDistribuido(Long productoId, int cantidadTotal) {
        Producto producto = productoService.buscarPorId(productoId)
                .orElseThrow(() -> new StockInsuficienteException("Producto no encontrado con ID: " + productoId));
        retirarStockDistribuido(producto, cantidadTotal);
    }

    @Override
    @Transactional
    public void ingresarStockDistribuido(Long productoId, int cantidadTotal) {
        Producto producto = productoService.buscarPorId(productoId)
                .orElseThrow(() -> new StockInsuficienteException("Producto no encontrado con ID: " + productoId));
        ingresarStockDistribuido(producto, cantidadTotal);
    }

    @Override
    @Transactional
    public MovimientoInventario transferirStock(Long productoId, Long ubicacionOrigenId, Long ubicacionDestinoId, int cantidad) {
    Producto producto = productoService.buscarPorId(productoId)
        .orElseThrow(() -> new StockInsuficienteException("Producto no encontrado con ID: " + productoId));

    Ubicacion origen = ubicacionRepositorio.findById(ubicacionOrigenId)
        .orElseThrow(() -> new StockInsuficienteException("Ubicación origen no encontrada con ID: " + ubicacionOrigenId));

    Ubicacion destino = ubicacionRepositorio.findById(ubicacionDestinoId)
        .orElseThrow(() -> new StockInsuficienteException("Ubicación destino no encontrada con ID: " + ubicacionDestinoId));

    return transferirStock(producto, origen, destino, cantidad);
    }

    @Override
    public int calcularStockTotal(Long productoId) {
        Producto producto = productoService.buscarPorId(productoId)
                .orElseThrow(() -> new StockInsuficienteException("Producto no encontrado con ID: " + productoId));
        return calcularStockTotal(producto);
    }

    @Override
    public boolean hayStockSuficiente(Long productoId, int cantidadSolicitada) {
        return calcularStockTotal(productoId) >= cantidadSolicitada;
    }

    @Transactional
    public MovimientoInventario ingresarStock(Producto producto, Ubicacion ubicacion, int cantidad) {
        validarCantidadPositiva(cantidad);
        validarCapacidadDisponible(ubicacion, cantidad);

        Inventario inventario = buscarOCrearInventario(producto, ubicacion);
        inventario.setCantidad(inventario.getCantidad() + cantidad);
        inventario.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(inventario);

        ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + cantidad);
        ubicacionRepositorio.save(ubicacion);

        return registrarMovimiento(producto, null, ubicacion, cantidad, EstadoMovimientoInventario.ENTRADA);
    }

    @Transactional
    public MovimientoInventario retirarStock(Producto producto, Ubicacion ubicacion, int cantidad) {
        validarCantidadPositiva(cantidad);

        Inventario inventario = buscarInventario(producto, ubicacion)
                .orElseThrow(() -> new StockInsuficienteException(
                        "No existe inventario del producto '" + producto.getNombre() +
                                "' en la ubicación " + ubicacion.getCodigo()));

        if (inventario.getCantidad() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente. Disponible: " + inventario.getCantidad() +
                            ", Solicitado: " + cantidad);
        }

        inventario.setCantidad(inventario.getCantidad() - cantidad);
        inventario.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(inventario);

        ubicacion.setOcupadoActual(Math.max(0, ubicacion.getOcupadoActual() - cantidad));
        ubicacionRepositorio.save(ubicacion);

        return registrarMovimiento(producto, ubicacion, null, cantidad, EstadoMovimientoInventario.SALIDA);
    }

    @Transactional
    public MovimientoInventario transferirStock(Producto producto, Ubicacion origen,
                                               Ubicacion destino, int cantidad) {
        validarCantidadPositiva(cantidad);
        validarCapacidadDisponible(destino, cantidad);

        Inventario invOrigen = buscarInventario(producto, origen)
                .orElseThrow(() -> new StockInsuficienteException(
                        "No existe inventario del producto '" + producto.getNombre() +
                                "' en la ubicación origen " + origen.getCodigo()));

        if (invOrigen.getCantidad() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente en origen. Disponible: " + invOrigen.getCantidad() +
                            ", Solicitado: " + cantidad);
        }

        invOrigen.setCantidad(invOrigen.getCantidad() - cantidad);
        invOrigen.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(invOrigen);

        origen.setOcupadoActual(Math.max(0, origen.getOcupadoActual() - cantidad));
        ubicacionRepositorio.save(origen);

        Inventario invDestino = buscarOCrearInventario(producto, destino);
        invDestino.setCantidad(invDestino.getCantidad() + cantidad);
        invDestino.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(invDestino);

        destino.setOcupadoActual(destino.getOcupadoActual() + cantidad);
        ubicacionRepositorio.save(destino);

        return registrarMovimiento(producto, origen, destino, cantidad, EstadoMovimientoInventario.REUBICACION);
    }

    @Transactional
    public void retirarStockDistribuido(Producto producto, int cantidadTotal) {
        validarCantidadPositiva(cantidadTotal);

        List<Inventario> inventarios = inventarioRepositorio
                .findAllByProductoId(producto.getIdProducto());

        int pendiente = cantidadTotal;

        for (Inventario inv : inventarios) {
            if (pendiente <= 0) break;
            if (inv.getCantidad() <= 0) continue;

            int aRetirar = Math.min(inv.getCantidad(), pendiente);

            inv.setCantidad(inv.getCantidad() - aRetirar);
            inv.setFecha_actualizacion(new Date());
            inventarioRepositorio.save(inv);

        Ubicacion ubicacion = ubicacionRepositorio.findById(inv.getUbicacionId())
            .orElseThrow(() -> new StockInsuficienteException(
                "Ubicación no encontrada con ID: " + inv.getUbicacionId()));
            ubicacion.setOcupadoActual(Math.max(0, ubicacion.getOcupadoActual() - aRetirar));
            ubicacionRepositorio.save(ubicacion);

            registrarMovimiento(producto, ubicacion, null, aRetirar, EstadoMovimientoInventario.SALIDA);

            pendiente -= aRetirar;
        }

        if (pendiente > 0) {
            throw new StockInsuficienteException(
                    "Stock insuficiente para '" + producto.getNombre() +
                            "'. Faltan " + pendiente + " unidades.");
        }
    }

    @Transactional
    public void ingresarStockDistribuido(Producto producto, int cantidadTotal) {
        validarCantidadPositiva(cantidadTotal);

        int restante = cantidadTotal;

        List<Inventario> existentes = inventarioRepositorio
        .findAllByProductoId(producto.getIdProducto());

        for (Inventario inv : existentes) {
            if (restante <= 0) break;

        Ubicacion ubicacion = ubicacionRepositorio.findById(inv.getUbicacionId())
            .orElseThrow(() -> new StockInsuficienteException(
                "Ubicación no encontrada con ID: " + inv.getUbicacionId()));
            int espacioLibre = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();

            if (espacioLibre > 0) {
                int aIngresar = Math.min(espacioLibre, restante);

                inv.setCantidad(inv.getCantidad() + aIngresar);
                inv.setFecha_actualizacion(new Date());
                inventarioRepositorio.save(inv);

                ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + aIngresar);
                ubicacionRepositorio.save(ubicacion);

                registrarMovimiento(producto, null, ubicacion, aIngresar, EstadoMovimientoInventario.ENTRADA);

                restante -= aIngresar;
            }
        }

    while (restante > 0) {
        // Hard boundary: ya no existe query por JOIN a Zona. Usamos una estrategia simple:
        // tomar cualquier ubicación con al menos 1 unidad de espacio libre.
        List<Ubicacion> disponibles = ubicacionRepositorio.findAll().stream()
            .filter(u -> (u.getCapacidadMaxima() - u.getOcupadoActual()) >= 1)
            .toList();

            if (disponibles.isEmpty()) {
                throw new CapacidadExcedida(
                        "No hay espacio disponible para " + restante +
                                " unidades de '" + producto.getNombre() + "'");
            }

            Ubicacion ubicacion = disponibles.get(0);
            int espacioLibre = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
            int aIngresar = Math.min(espacioLibre, restante);

            Inventario nuevoInv = new Inventario();
            nuevoInv.setProductoId(producto.getIdProducto());
            nuevoInv.setUbicacionId(ubicacion.getIdUbicacion());
            nuevoInv.setCantidad(aIngresar);
            nuevoInv.setFecha_actualizacion(new Date());
            inventarioRepositorio.save(nuevoInv);

            ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + aIngresar);
            ubicacionRepositorio.save(ubicacion);

            registrarMovimiento(producto, null, ubicacion, aIngresar, EstadoMovimientoInventario.ENTRADA);

            restante -= aIngresar;
        }
    }

    public int calcularStockTotal(Producto producto) {
        return inventarioRepositorio.findAllByProductoId(producto.getIdProducto())
                .stream()
                .mapToInt(Inventario::getCantidad)
                .sum();
    }

    public boolean hayStockSuficiente(Producto producto, int cantidadSolicitada) {
        return calcularStockTotal(producto) >= cantidadSolicitada;
    }

    private Optional<Inventario> buscarInventario(Producto producto, Ubicacion ubicacion) {
        return inventarioRepositorio.findByProductoIdAndUbicacionId(producto.getIdProducto(), ubicacion.getIdUbicacion());
    }

    private Inventario buscarOCrearInventario(Producto producto, Ubicacion ubicacion) {
        return inventarioRepositorio.findByProductoIdAndUbicacionId(producto.getIdProducto(), ubicacion.getIdUbicacion())
                .orElseGet(() -> {
                    Inventario inv = new Inventario();
                    inv.setProductoId(producto.getIdProducto());
                    inv.setUbicacionId(ubicacion.getIdUbicacion());
                    inv.setCantidad(0);
                    inv.setFecha_actualizacion(new Date());
                    return inv;
                });
    }

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }

    private void validarCapacidadDisponible(Ubicacion ubicacion, int cantidad) {
        int espacioLibre = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
        if (espacioLibre < cantidad) {
            throw new CapacidadExcedida(
                    "Capacidad excedida. Espacio disponible: " + espacioLibre +
                            ", Requerido: " + cantidad);
        }
    }

    private MovimientoInventario registrarMovimiento(Producto producto,
                                                    Ubicacion origen,
                                                    Ubicacion destino,
                                                    int cantidad,
                                                    EstadoMovimientoInventario estado) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setProductoId(producto != null ? producto.getIdProducto() : null);
        movimiento.setUbicacionOrigenId(origen != null ? origen.getIdUbicacion() : null);
        movimiento.setUbicacionDestinoId(destino != null ? destino.getIdUbicacion() : null);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(new Date());
        movimiento.setEstado(estado);
        return movimientoRepositorio.save(movimiento);
    }
}
