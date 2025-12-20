package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {
    Inventario findInventarioByProducto_IdProducto(Long idProducto);
    List<Inventario> getInventariosByUbicacion(Ubicacion ubicacion);
    List<Inventario> findAllByProducto_CodigoSku(String codigoSku);
    List<Inventario> findAllByProducto_IdProducto(Long idProducto);
    
    /**
     * Busca inventario específico por producto y ubicación.
     * Usado por StockDomainService para operaciones atómicas de stock.
     */
    Optional<Inventario> findByProductoAndUbicacion(Producto producto, Ubicacion ubicacion);
}
