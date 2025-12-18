package com.juan.curso.springboot.webapp.gestordedepositos.modules.products.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProductoRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.GenericService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements GenericService<Producto, Long> {

    private final ProductoRepositorio productoRepositorio;

    public ProductoServiceImpl(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    @Override
    public Optional<List<Producto>> buscarTodos() {
        try {
            return Optional.of(productoRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        try {
            return productoRepositorio.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Producto crear(Producto producto) {
        if (productoRepositorio.existsByCodigoSku(producto.getCodigoSku())) {
            throw new RuntimeException("El código SKU '" + producto.getCodigoSku() + "' ya existe en el sistema.");
        }

        try {
            return productoRepositorio.save(producto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el producto en base de datos");
        }
    }

    @Override
    @Transactional
    public Producto actualizar(Producto productoEditado) {
        Producto productoActual = productoRepositorio.findById(productoEditado.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!productoActual.getCodigoSku().equalsIgnoreCase(productoEditado.getCodigoSku())) {
            if (productoRepositorio.existsByCodigoSku(productoEditado.getCodigoSku())) {
                throw new RuntimeException("El código SKU '" + productoEditado.getCodigoSku() + "' ya existe.");
            }
        }

        try {
            return productoRepositorio.save(productoEditado);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el producto");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        try {
            Optional<Producto> producto = buscarPorId(id);
            if (producto.isPresent()) {
                producto.get().setIsDeleted("S");
                productoRepositorio.save(producto.get());
            } else {
                throw new RuntimeException("No se pudo eliminar: Producto no encontrado");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar producto");
        }
    }

    public Producto buscarPorCodigoSKU(String codigo) {
        return productoRepositorio.findProductoByCodigoSkuIs(codigo);
    }
}
