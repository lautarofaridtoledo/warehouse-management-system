package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Usuario getByNombreEquals(String nombre);
    List<Usuario> getByRolId(Long rolId);
    Usuario getUsuarioByIdUsuarioEquals(Long idUsuario);
    Usuario getByEmailEquals(String email);
    boolean existsByNombre(String nombre);
    boolean existsByEmail(String email);
}
