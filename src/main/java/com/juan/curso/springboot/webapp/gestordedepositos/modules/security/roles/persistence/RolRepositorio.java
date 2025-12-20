package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}