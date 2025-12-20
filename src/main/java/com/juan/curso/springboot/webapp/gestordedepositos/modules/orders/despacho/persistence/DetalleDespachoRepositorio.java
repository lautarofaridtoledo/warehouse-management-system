package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleDespachoRepositorio extends JpaRepository<DetalleDespacho, Long> {
}
