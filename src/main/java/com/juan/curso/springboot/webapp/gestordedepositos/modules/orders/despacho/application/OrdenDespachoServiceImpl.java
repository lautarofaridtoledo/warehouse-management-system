package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.OrdenDespachoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.OrdenDespachoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ClienteServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.GenericService;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.domain.StockDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Application service del módulo Orders/Despacho.
 *
 * Nota: este service implementa la lógica (copiada del legacy) para evitar
 * ciclos de beans entre wrappers y módulo.
 */
@Service("ordenDespachoService")
public class OrdenDespachoServiceImpl implements GenericService<OrdenDespacho, Long> {

    private final OrdenDespachoRepositorio ordenDespachoRepositorio;
    private final ClienteServiceImpl clienteService;
    private final ProductoServiceImpl productoService;
    private final StockDomainService stockDomainService;

    public OrdenDespachoServiceImpl(OrdenDespachoRepositorio ordenDespachoRepositorio,
                                    ClienteServiceImpl clienteService,
                                    ProductoServiceImpl productoService,
                                    StockDomainService stockDomainService) {
        this.ordenDespachoRepositorio = ordenDespachoRepositorio;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.stockDomainService = stockDomainService;
    }

    @Override
    public Optional<List<OrdenDespacho>> buscarTodos() {
        return Optional.of(ordenDespachoRepositorio.findAll());
    }

    @Override
    public Optional<OrdenDespacho> buscarPorId(Long id) {
        return ordenDespachoRepositorio.findById(id);
    }

    @Override
    public void eliminar(Long id) {
        ordenDespachoRepositorio.deleteById(id);
    }

    @Override
    public OrdenDespacho crear(OrdenDespacho entity) {
        return ordenDespachoRepositorio.save(entity);
    }

    @Override
    public OrdenDespacho actualizar(OrdenDespacho entity) {
        return ordenDespachoRepositorio.save(entity);
    }

    public boolean ExistePorId(Long id) {
        return ordenDespachoRepositorio.existsById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrdenDespacho procesarSalidaMercaderia(OrdenDespachoDTO dto) {
        Cliente cliente = clienteService.buscarPorId(dto.getCliente().getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        OrdenDespacho orden = new OrdenDespacho();
        orden.setCliente(cliente);
        orden.setFechaDespacho(dto.getFechaDespacho());
        orden.setEstado(dto.getEstado());

        List<DetalleDespacho> detalles = new ArrayList<>();

        for (var detalleDto : dto.getDetalle_despacho()) {
            Producto producto = productoService.buscarPorId(detalleDto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            if (!stockDomainService.hayStockSuficiente(producto, detalleDto.getCantidad())) {
                throw new RuntimeException("Stock insuficiente para " + producto.getNombre() +
                        ". Disponible: " + stockDomainService.calcularStockTotal(producto));
            }

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setOrdenDespacho(orden);
            detalles.add(detalle);

            stockDomainService.retirarStockDistribuido(producto, detalleDto.getCantidad());
        }

        orden.setDetalleDespacho(detalles);
        return ordenDespachoRepositorio.save(orden);
    }

    @Transactional(rollbackFor = Exception.class)
    public OrdenDespacho procesarModificacionOrden(Long idOrden, OrdenDespachoDTO dto) {
        OrdenDespacho ordenActual = ordenDespachoRepositorio.findById(idOrden)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        if (ordenActual.getEstado() == EstadosDeOrden.COMPLETADA) {
            throw new RuntimeException("No se puede editar una orden COMPLETADA.");
        }

        for (DetalleDespacho detalleViejo : ordenActual.getDetalleDespacho()) {
            stockDomainService.ingresarStockDistribuido(detalleViejo.getProducto(), detalleViejo.getCantidad());
        }

        Cliente cliente = clienteService.buscarPorId(dto.getCliente().getIdCliente())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        ordenActual.setCliente(cliente);
        ordenActual.setFechaDespacho(dto.getFechaDespacho());
        ordenActual.setEstado(dto.getEstado());

        ordenActual.getDetalleDespacho().clear();
        ordenDespachoRepositorio.saveAndFlush(ordenActual);

        List<DetalleDespacho> nuevosDetalles = new ArrayList<>();

        for (var detalleDto : dto.getDetalle_despacho()) {
            Producto producto = productoService.buscarPorId(detalleDto.getProducto().getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

            DetalleDespacho detalle = new DetalleDespacho();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setOrdenDespacho(ordenActual);
            nuevosDetalles.add(detalle);

            stockDomainService.retirarStockDistribuido(producto, detalleDto.getCantidad());
        }

        ordenActual.getDetalleDespacho().addAll(nuevosDetalles);
        return ordenDespachoRepositorio.save(ordenActual);
    }

    @Transactional
    public void eliminarConReversion(Long id) {
        OrdenDespacho orden = ordenDespachoRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden no encontrada"));

        for (DetalleDespacho detalle : orden.getDetalleDespacho()) {
            stockDomainService.ingresarStockDistribuido(detalle.getProducto(), detalle.getCantidad());
        }
        ordenDespachoRepositorio.delete(orden);
    }
}
