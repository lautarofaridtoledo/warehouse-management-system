package com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.MovimientoInventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio que centraliza todas las operaciones de stock.
 * Garantiza consistencia entre Inventario, Ubicacion y MovimientoInventario.
 * 
 * Operaciones principales:
 * - ingresarStock: entrada de mercadería (recepciones)
 * - retirarStock: salida de mercadería (despachos)
 * - transferirStock: reubicación entre ubicaciones
 */
@Service
public class StockDomainService {

    private final InventarioRepositorio inventarioRepositorio;
    private final UbicacionRepositorio ubicacionRepositorio;
    private final MovimientoInventarioRepositorio movimientoRepositorio;

    public StockDomainService(InventarioRepositorio inventarioRepositorio,
                              UbicacionRepositorio ubicacionRepositorio,
                              MovimientoInventarioRepositorio movimientoRepositorio) {
        this.inventarioRepositorio = inventarioRepositorio;
        this.ubicacionRepositorio = ubicacionRepositorio;
        this.movimientoRepositorio = movimientoRepositorio;
    }

    /**
     * Ingresa stock a una ubicación específica (usado por recepciones).
     * Crea inventario si no existe, actualiza ocupación y registra movimiento.
     *
     * @param producto  Producto a ingresar
     * @param ubicacion Ubicación destino
     * @param cantidad  Cantidad a ingresar (debe ser > 0)
     * @return MovimientoInventario registrado
     * @throws CapacidadExcedida si no hay espacio suficiente
     */
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

    /**
     * Retira stock de una ubicación específica (usado por despachos).
     * Valida stock suficiente, actualiza ocupación y registra movimiento.
     *
     * @param producto  Producto a retirar
     * @param ubicacion Ubicación origen
     * @param cantidad  Cantidad a retirar (debe ser > 0)
     * @return MovimientoInventario registrado
     * @throws StockInsuficienteException si no hay stock suficiente
     */
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

    /**
     * Transfiere stock entre ubicaciones (reubicación).
     * Combina retiro de origen e ingreso en destino atómicamente.
     *
     * @param producto Producto a transferir
     * @param origen   Ubicación origen
     * @param destino  Ubicación destino
     * @param cantidad Cantidad a transferir (debe ser > 0)
     * @return MovimientoInventario registrado
     * @throws StockInsuficienteException si no hay stock suficiente en origen
     * @throws CapacidadExcedida si no hay espacio suficiente en destino
     */
    @Transactional
    public MovimientoInventario transferirStock(Producto producto, Ubicacion origen, 
                                                 Ubicacion destino, int cantidad) {
        validarCantidadPositiva(cantidad);
        validarCapacidadDisponible(destino, cantidad);

        // Validar stock en origen
        Inventario invOrigen = buscarInventario(producto, origen)
                .orElseThrow(() -> new StockInsuficienteException(
                        "No existe inventario del producto '" + producto.getNombre() + 
                        "' en la ubicación origen " + origen.getCodigo()));

        if (invOrigen.getCantidad() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente en origen. Disponible: " + invOrigen.getCantidad() + 
                    ", Solicitado: " + cantidad);
        }

        // Restar de origen
        invOrigen.setCantidad(invOrigen.getCantidad() - cantidad);
        invOrigen.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(invOrigen);

        origen.setOcupadoActual(Math.max(0, origen.getOcupadoActual() - cantidad));
        ubicacionRepositorio.save(origen);

        // Sumar a destino
        Inventario invDestino = buscarOCrearInventario(producto, destino);
        invDestino.setCantidad(invDestino.getCantidad() + cantidad);
        invDestino.setFecha_actualizacion(new Date());
        inventarioRepositorio.save(invDestino);

        destino.setOcupadoActual(destino.getOcupadoActual() + cantidad);
        ubicacionRepositorio.save(destino);

        return registrarMovimiento(producto, origen, destino, cantidad, EstadoMovimientoInventario.REUBICACION);
    }

    /**
     * Retira stock de múltiples ubicaciones hasta completar la cantidad (FIFO).
     * Útil para despachos donde no se especifica ubicación.
     *
     * @param producto      Producto a retirar
     * @param cantidadTotal Cantidad total a retirar
     * @throws StockInsuficienteException si no hay stock suficiente en total
     */
    @Transactional
    public void retirarStockDistribuido(Producto producto, int cantidadTotal) {
        validarCantidadPositiva(cantidadTotal);

        List<Inventario> inventarios = inventarioRepositorio
                .findAllByProducto_IdProducto(producto.getIdProducto());
        
        int pendiente = cantidadTotal;

        for (Inventario inv : inventarios) {
            if (pendiente <= 0) break;
            if (inv.getCantidad() <= 0) continue;

            int aRetirar = Math.min(inv.getCantidad(), pendiente);

            inv.setCantidad(inv.getCantidad() - aRetirar);
            inv.setFecha_actualizacion(new Date());
            inventarioRepositorio.save(inv);

            Ubicacion ubicacion = inv.getUbicacion();
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

    /**
     * Ingresa stock distribuyendo en ubicaciones disponibles según categoría.
     * Primero intenta llenar ubicaciones donde ya existe el producto,
     * luego busca nuevas ubicaciones compatibles.
     *
     * @param producto      Producto a ingresar
     * @param cantidadTotal Cantidad total a ingresar
     * @throws CapacidadExcedida si no hay espacio suficiente
     */
    @Transactional
    public void ingresarStockDistribuido(Producto producto, int cantidadTotal) {
        validarCantidadPositiva(cantidadTotal);

        int restante = cantidadTotal;

        // Primero intentar en ubicaciones donde ya existe el producto
        List<Inventario> existentes = inventarioRepositorio
                .findAllByProducto_IdProducto(producto.getIdProducto());

        for (Inventario inv : existentes) {
            if (restante <= 0) break;

            Ubicacion ubicacion = inv.getUbicacion();
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

        // Si queda restante, buscar nuevas ubicaciones por categoría
        while (restante > 0) {
            List<Ubicacion> disponibles = ubicacionRepositorio.buscarUbicacionesPorCategoriaYEspacio(
                    producto.getCategoria(), 1);

            if (disponibles.isEmpty()) {
                throw new CapacidadExcedida(
                        "No hay espacio disponible para " + restante + 
                        " unidades de '" + producto.getNombre() + "'");
            }

            Ubicacion ubicacion = disponibles.get(0);
            int espacioLibre = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
            int aIngresar = Math.min(espacioLibre, restante);

            Inventario nuevoInv = new Inventario();
            nuevoInv.setProducto(producto);
            nuevoInv.setUbicacion(ubicacion);
            nuevoInv.setCantidad(aIngresar);
            nuevoInv.setFecha_actualizacion(new Date());
            inventarioRepositorio.save(nuevoInv);

            ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + aIngresar);
            ubicacionRepositorio.save(ubicacion);

            registrarMovimiento(producto, null, ubicacion, aIngresar, EstadoMovimientoInventario.ENTRADA);

            restante -= aIngresar;
        }
    }

    /**
     * Calcula el stock total de un producto sumando todos sus inventarios.
     *
     * @param producto Producto a consultar
     * @return Stock total disponible
     */
    public int calcularStockTotal(Producto producto) {
        return inventarioRepositorio.findAllByProducto_IdProducto(producto.getIdProducto())
                .stream()
                .mapToInt(Inventario::getCantidad)
                .sum();
    }

    /**
     * Verifica si hay stock suficiente de un producto.
     *
     * @param producto Producto a verificar
     * @param cantidad Cantidad requerida
     * @return true si hay stock suficiente
     */
    public boolean hayStockSuficiente(Producto producto, int cantidad) {
        return calcularStockTotal(producto) >= cantidad;
    }

    // =============== Métodos auxiliares privados ===============

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }

    private void validarCapacidadDisponible(Ubicacion ubicacion, int cantidad) {
        int espacioLibre = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();
        if (espacioLibre < cantidad) {
            throw new CapacidadExcedida(
                    "Capacidad insuficiente en " + ubicacion.getCodigo() +
                    ". Disponible: " + espacioLibre + ", Requerido: " + cantidad);
        }
    }

    private Optional<Inventario> buscarInventario(Producto producto, Ubicacion ubicacion) {
        return inventarioRepositorio.findByProductoAndUbicacion(producto, ubicacion);
    }

    private Inventario buscarOCrearInventario(Producto producto, Ubicacion ubicacion) {
        return buscarInventario(producto, ubicacion)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();
                    nuevo.setProducto(producto);
                    nuevo.setUbicacion(ubicacion);
                    nuevo.setCantidad(0);
                    return nuevo;
                });
    }

    private MovimientoInventario registrarMovimiento(Producto producto, Ubicacion origen,
                                                      Ubicacion destino, int cantidad,
                                                      EstadoMovimientoInventario estado) {
        MovimientoInventario mov = new MovimientoInventario();
        mov.setProducto(producto);
        mov.setUbicacionOrigen(origen);
        mov.setUbicacionDestino(destino);
        mov.setCantidad(cantidad);
        mov.setEstado(estado);
        mov.setFecha(new Date());
        return movimientoRepositorio.save(mov);
    }
}
