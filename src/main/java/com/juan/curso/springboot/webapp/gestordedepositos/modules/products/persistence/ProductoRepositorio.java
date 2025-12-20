package com.juan.curso.springboot.webapp.gestordedepositos.modules.products.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
    Producto findProductoByCodigoSkuIs(String codigo);
    boolean existsByCodigoSku(String codigoSku);
}
