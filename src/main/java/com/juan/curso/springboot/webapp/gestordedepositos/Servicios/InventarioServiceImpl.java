package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.*;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.InventarioRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain.StockDomainService;
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
    ProductoServiceImpl productoService;
    @Autowired
    StockDomainService stockDomainService;

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
        // Delega a StockDomainService que maneja todo: inventario, ubicación y movimiento
        stockDomainService.retirarStockDistribuido(
                detalleDespacho.getProducto(), 
                detalleDespacho.getCantidad()
        );
        return buscarInventariosPorIdProducto(detalleDespacho.getProducto().getIdProducto());
    }

    @Transactional
    public void deshacerIngreso(Producto producto, int cantidad) {
        // Delega a StockDomainService
        stockDomainService.retirarStockDistribuido(producto, cantidad);
    }

    /**
     * Agrega mercadería distribuyendo en ubicaciones disponibles.
     * Delega completamente a StockDomainService.
     */
    @Transactional
    public void agregarMercaderiaConProductoPersistido(Producto producto, int cantidad) {
        stockDomainService.ingresarStockDistribuido(producto, cantidad);
    }
}