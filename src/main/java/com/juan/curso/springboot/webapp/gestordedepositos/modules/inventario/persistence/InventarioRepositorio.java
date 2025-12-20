package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepositorio extends JpaRepository<Inventario, Long> {
    Inventario findInventarioByProductoId(Long productoId);
    List<Inventario> getInventariosByUbicacionId(Long ubicacionId);
    List<Inventario> findAllByProductoId(Long productoId);
    
    /**
     * Busca inventario específico por producto y ubicación.
     * Usado por StockDomainService para operaciones atómicas de stock.
     */
    Optional<Inventario> findByProductoIdAndUbicacionId(Long productoId, Long ubicacionId);
}
