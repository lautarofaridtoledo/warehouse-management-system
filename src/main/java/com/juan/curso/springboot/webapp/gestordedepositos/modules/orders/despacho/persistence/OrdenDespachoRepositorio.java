package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenDespachoRepositorio extends JpaRepository<OrdenDespacho, Long> {
}
