package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleRecepcionRepositorio extends JpaRepository<DetalleRecepcion, Long> {
    Optional<List<DetalleRecepcion>> findByOrdenRecepcion_IdOrdenRecepcion(Long idOrden);
}
