package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.DetalleRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenRecepcionDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenRecepcionRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain.StockDomainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenRecepcionServiceImpl implements GenericService<OrdenRecepcion, Long>{

    private final OrdenRecepcionRepositorio ordenRecepcionRepositorio;
    private final ProveedorServiceImpl proveedorService;
    private final ProductoServiceImpl productoService;
    private final StockDomainService stockDomainService;

    @Autowired
    public OrdenRecepcionServiceImpl(OrdenRecepcionRepositorio ordenRecepcionRepositorio,
                                     ProveedorServiceImpl proveedorService,
                                     ProductoServiceImpl productoService,
                                     StockDomainService stockDomainService) {
        this.ordenRecepcionRepositorio = ordenRecepcionRepositorio;
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.stockDomainService = stockDomainService;
    }

    @Override
    public Optional<List<OrdenRecepcion>> buscarTodos() {
        Optional<List<OrdenRecepcion>> ordenes = Optional.of(new ArrayList<>(ordenRecepcionRepositorio.findAll()));
        return ordenes;
    }

    @Override
    public Optional<OrdenRecepcion> buscarPorId(Long id) {
        Optional<OrdenRecepcion> orden = ordenRecepcionRepositorio.findById(id);
        return orden;
    }

    @Override
    public OrdenRecepcion crear(OrdenRecepcion ordenRecepcion) {

        try {
            ordenRecepcion = ordenRecepcionRepositorio.save(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Override
    public OrdenRecepcion actualizar(OrdenRecepcion ordenRecepcion) {
        try {
           ordenRecepcion = ordenRecepcionRepositorio.saveAndFlush(ordenRecepcion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordenRecepcion;
    }

    @Transactional
    @Override
    public void eliminar(Long id) {
        try {
            ordenRecepcionRepositorio.deleteById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public OrdenRecepcion procesarEntradaMercaderia(OrdenRecepcionDTO dto) {

        Proveedor proveedor = proveedorService.buscarPorId(dto.getProveedor().getId_proveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));

        OrdenRecepcion orden = new OrdenRecepcion();
        orden.setProveedor(proveedor);
        orden.setFecha(Calendar.getInstance().getTime());
        orden.setEstado(dto.getEstado());

        List<DetalleRecepcion> detalles = new ArrayList<>();

        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            String sku = detalleDTO.getProducto().getCodigoSku();
            Producto producto = productoService.buscarPorCodigoSKU(sku);

            if (producto == null) {
                Producto nuevo = detalleDTO.getProducto();
                nuevo.setIsDeleted("N");
                producto = productoService.crear(nuevo);
            }

            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(orden);
            detalles.add(detalle);

            // Delega a StockDomainService para ingreso de stock distribuido
            stockDomainService.ingresarStockDistribuido(producto, detalleDTO.getCantidad());
        }

        orden.setDetallesRecepcion(detalles);

        return ordenRecepcionRepositorio.save(orden);
    }

    @Transactional
    public OrdenRecepcion procesarModificacionOrden(Long idOrden, OrdenRecepcionDTO dto) {
        OrdenRecepcion ordenActual = ordenRecepcionRepositorio.findById(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        if (ordenActual.getEstado() == EstadosDeOrden.COMPLETADA) {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }

        // Revertir stock de detalles anteriores
        for (DetalleRecepcion detalleViejo : ordenActual.getDetallesRecepcion()) {
            stockDomainService.retirarStockDistribuido(detalleViejo.getProducto(), detalleViejo.getCantidad());
        }

        Proveedor proveedor = proveedorService.buscarPorId(dto.getProveedor().getId_proveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado"));
        ordenActual.setProveedor(proveedor);
        ordenActual.setFecha(dto.getFecha());
        ordenActual.setEstado(dto.getEstado());

        ordenActual.getDetallesRecepcion().clear();

        ordenRecepcionRepositorio.saveAndFlush(ordenActual);
        List<DetalleRecepcion> nuevosDetalles = new ArrayList<>();
        for (DetalleRecepcionDTO detalleDTO : dto.getDetalleRecepcionDTOList()) {
            DetalleRecepcion detalle = new DetalleRecepcion();

            Producto producto = productoService.buscarPorCodigoSKU(detalleDTO.getProducto().getCodigoSku());
            if (producto == null) {
                Producto nuevo = detalleDTO.getProducto();
                nuevo.setIsDeleted("N");
                producto = productoService.crear(nuevo);
            }

            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setOrdenRecepcion(ordenActual);
            nuevosDetalles.add(detalle);

            // Ingresar nuevo stock
            stockDomainService.ingresarStockDistribuido(producto, detalleDTO.getCantidad());
        }

        ordenActual.getDetallesRecepcion().addAll(nuevosDetalles);

        return ordenRecepcionRepositorio.save(ordenActual);
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminarConReversion(Long id) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada con ID: " + id));

        // Revertir stock: retirar la mercadería que esta orden ingresó
        for (DetalleRecepcion detalle : orden.getDetallesRecepcion()) {
            stockDomainService.retirarStockDistribuido(
                    detalle.getProducto(),
                    detalle.getCantidad()
            );
        }

        ordenRecepcionRepositorio.delete(orden);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrderState(Long id, EstadosDeOrden estado) {
        OrdenRecepcion orden = ordenRecepcionRepositorio.findById(id).orElseThrow(()->new RecursoNoEncontradoException("Orden no encontrado"));
        if(orden.getEstado() == EstadosDeOrden.PENDIENTE && estado == EstadosDeOrden.COMPLETADA) {
            orden.setEstado(estado);
        } else {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }
    }
}
