package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.domain.StockDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso / application service para operaciones de Inventario.
 * Mantiene la API existente (métodos) para facilitar migración incremental.
 */
@Service("inventarioService")
public class InventarioServiceImpl implements GenericService<Inventario, Long> {

    @Autowired
    InventarioRepositorio inventarioRepositorio;

    @Autowired
    ProductoServiceImpl productoService;

    @Autowired
    StockDomainService stockDomainService;

    public InventarioServiceImpl() {
    }

    @Override
    public Optional<List<Inventario>> buscarTodos() {
        try {
            return Optional.of(inventarioRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Inventario> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return inventarioRepositorio.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Inventario crear(Inventario inventario) {
        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
        return inventarioRepositorio.save(inventario);
    }

    @Override
    public Inventario actualizar(Inventario inventario) {
        inventario.setFecha_actualizacion(Calendar.getInstance().getTime());
        return inventarioRepositorio.save(inventario);
    }

    @Override
    public void eliminar(Long id) {
        inventarioRepositorio.deleteById(id);
    }

    public List<Inventario> buscarInventariosPorIdProducto(Long idProducto) throws RecursoNoEncontradoException {
        String sku = productoService.buscarPorId(idProducto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"))
                .getCodigoSku();
        return inventarioRepositorio.findAllByProducto_CodigoSku(sku);
    }

    public List<Inventario> buscarPorCodigoSku(String codigoSku) {
        return inventarioRepositorio.findAllByProducto_CodigoSku(codigoSku);
    }

    public int calcularStockTotalPorIdProducto(Long idProducto) {
        List<Inventario> inventarios = buscarInventariosPorIdProducto(idProducto);
        return inventarios.stream().mapToInt(Inventario::getCantidad).sum();
    }

    public int calcularStockTotalPorCodigoSku(String codigoSku) {
        List<Inventario> inventarios = buscarPorCodigoSku(codigoSku);
        return inventarios.stream().mapToInt(Inventario::getCantidad).sum();
    }

    @Transactional
    public List<Inventario> disminuirCantidad(DetalleDespacho detalleDespacho) {
        stockDomainService.retirarStockDistribuido(
                detalleDespacho.getProducto(),
                detalleDespacho.getCantidad()
        );
        return buscarInventariosPorIdProducto(detalleDespacho.getProducto().getIdProducto());
    }

    @Transactional
    public void deshacerIngreso(Producto producto, int cantidad) {
        stockDomainService.retirarStockDistribuido(producto, cantidad);
    }

    @Transactional
    public void agregarMercaderiaConProductoPersistido(Producto producto, int cantidad) {
        stockDomainService.ingresarStockDistribuido(producto, cantidad);
    }
}
