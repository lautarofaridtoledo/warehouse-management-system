package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.StockInsuficienteException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.UbicacionRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioServiceImpl implements GenericService<Inventario, Long> {

    @Autowired
    InventarioRepositorio inventarioRepositorio;
    @Autowired
    MovimientoInventarioServiceImpl movimientoInventarioService;
    @Autowired
    UbicacionServiceImpl ubicacionService;
    @Autowired
    ProductoServiceImpl productoService;
    @Autowired
    private UbicacionRepositorio ubicacionRepositorio;

    public InventarioServiceImpl() {
    }

    @Override
    public Optional<List<Inventario>> buscarTodos() {
        try{ return Optional.of(inventarioRepositorio.findAll()); } catch (Exception e){ e.printStackTrace(); } return Optional.empty();
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try { return inventarioRepositorio.findById(id); } catch (Exception e){ e.printStackTrace(); return Optional.empty(); }
    }

    @Override
    public Inventario crear(Inventario inventario) {
        try{
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            return inventarioRepositorio.save(inventario);
        } catch (RuntimeException e) { throw new RuntimeException(e); }
    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        try{
            inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
            return inventarioRepositorio.save(inventario);
        } catch (RuntimeException e) { throw new RuntimeException(e); }
    }

    @Override
    public void eliminar(Long id) {
        try{ inventarioRepositorio.deleteById(id); } catch (RuntimeException e) { throw new RuntimeException(e); }
    }

    public List<Inventario> buscarInventariosPorIdProducto(Long idProducto) throws RecursoNoEncontradoException{
        try {
            String sku = productoService.buscarPorId(idProducto).get().getCodigoSku();
            return inventarioRepositorio.findAllByProducto_CodigoSku(sku);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<Inventario> buscarPorCodigoSku(String codigoSku) {
        return inventarioRepositorio.findAllByProducto_CodigoSku(codigoSku);
    }

    public int calcularStockTotalPorIdProducto(Long idProducto) {
        try {
            List<Inventario> inventarios = buscarInventariosPorIdProducto(idProducto);
            return inventarios.stream().mapToInt(Inventario::getCantidad).sum();
        } catch (Exception e) { return 0; }
    }

    public int calcularStockTotalPorCodigoSku(String codigoSku) {
        try {
            List<Inventario> inventarios = buscarPorCodigoSku(codigoSku);
            return inventarios.stream().mapToInt(Inventario::getCantidad).sum();
        } catch (Exception e) { return 0; }
    }

    @Transactional
    public List<Inventario> disminuirCantidad(DetalleDespacho detalleDespacho) {
        restarStockGenerico(detalleDespacho.getProducto(), detalleDespacho.getCantidad());

        return buscarInventariosPorIdProducto(detalleDespacho.getProducto().getIdProducto());
    }

    @Transactional
    public void deshacerIngreso(Producto producto, int cantidad) {
        restarStockGenerico(producto, cantidad);
    }

    private void restarStockGenerico(Producto producto, int cantidadTotalADescontar) {

        List<Inventario> inventarios = buscarInventariosPorIdProducto(producto.getIdProducto());
        int cantidadPendiente = cantidadTotalADescontar;

        for (Inventario inventario : inventarios) {
            if (cantidadPendiente <= 0) break;

            int stockActual = inventario.getCantidad();

            if (stockActual > 0) {
                int aRestar = Math.min(stockActual, cantidadPendiente);

                inventario.setCantidad(stockActual - aRestar);
                inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(inventario);

                Ubicacion origen = inventario.getUbicacion();
                int nuevoOcupado = origen.getOcupadoActual() - aRestar;
                origen.setOcupadoActual(Math.max(0, nuevoOcupado));
                ubicacionRepositorio.save(origen);

                // C. Registrar Movimiento (Salida)
                movimientoInventarioService.registrarMovimiento(
                        producto,
                        origen,
                        null,
                        aRestar,
                        EstadoMovimientoInventario.SALIDA
                );

                cantidadPendiente -= aRestar;
            }
        }

        if (cantidadPendiente > 0) {
            throw new StockInsuficienteException(
                    "No se puede completar la operación. Faltan " + cantidadPendiente +
                            " unidades del producto '" + producto.getNombre() + "' en el inventario."
            );
        }
    }

    // 3. MÉTODO PARA RECEPCIONES (Usa Categorías)
    @Transactional
    public void agregarMercaderiaConProductoPersistido(Producto producto, int cantidad) {
        int restante = cantidad;

        List<Inventario> inventariosExistentes = inventarioRepositorio.findAllByProducto_IdProducto(producto.getIdProducto());

        for (Inventario inv : inventariosExistentes) {
            Ubicacion ubicacion = inv.getUbicacion();
            int espacio = ubicacion.getCapacidadMaxima() - ubicacion.getOcupadoActual();

            if (espacio > 0) {
                int agregar = Math.min(espacio, restante);

                inv.setCantidad(inv.getCantidad() + agregar);
                inv.setFecha_actualizacion(Calendar.getInstance().getTime());
                inventarioRepositorio.save(inv);

                ubicacion.setOcupadoActual(ubicacion.getOcupadoActual() + agregar);
                ubicacionService.actualizar(ubicacion);

                movimientoInventarioService.registrarMovimiento(
                        producto,
                        null,
                        ubicacion,
                        agregar,
                        EstadoMovimientoInventario.ENTRADA
                );

                restante -= agregar;
                if (restante == 0) return;
            }
        }

        while (restante > 0) {

            List<Ubicacion> ubicacionesDisponibles = ubicacionRepositorio.buscarUbicacionesPorCategoriaYEspacio(producto.getCategoria(), 1);

            if (ubicacionesDisponibles.isEmpty()) {
                throw new CapacidadExcedida("No hay suficiente espacio en las Zonas de categoría: " + producto.getCategoria());
            }

            Ubicacion ubicacionDestino = ubicacionesDisponibles.get(0);

            int espacio = ubicacionDestino.getCapacidadMaxima() - ubicacionDestino.getOcupadoActual();
            int agregar = Math.min(espacio, restante);

            Inventario nuevo = new Inventario();
            nuevo.setProducto(producto);
            nuevo.setCantidad(agregar);
            nuevo.setUbicacion(ubicacionDestino);
            nuevo.setFecha_actualizacion(Calendar.getInstance().getTime());
            inventarioRepositorio.save(nuevo);

            ubicacionDestino.setOcupadoActual(ubicacionDestino.getOcupadoActual() + agregar);
            ubicacionService.actualizar(ubicacionDestino);

            movimientoInventarioService.registrarMovimiento(
                    producto,
                    null,
                    ubicacionDestino,
                    agregar,
                    EstadoMovimientoInventario.ENTRADA
            );

            restante -= agregar;
        }
    }
}