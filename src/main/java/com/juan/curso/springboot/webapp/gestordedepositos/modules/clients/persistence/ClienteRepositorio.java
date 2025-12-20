package com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
}
