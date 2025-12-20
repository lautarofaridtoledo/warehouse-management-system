package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRecepcionRepositorio extends JpaRepository<OrdenRecepcion, Long> {

}
