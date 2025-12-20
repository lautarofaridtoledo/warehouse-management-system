package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, Long> {
}
