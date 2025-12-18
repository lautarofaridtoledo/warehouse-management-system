package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Application service para Rol.
 * Implementación real (copiada del legacy) para evitar ciclos.
 */
@Service("rolService")
public class RolServiceImpl implements GenericService<Rol, Long> {

    private final RolRepositorio rolRepositorio;

    public RolServiceImpl(RolRepositorio rolRepositorio) {
        this.rolRepositorio = rolRepositorio;
    }

    @Override
    public Optional<List<Rol>> buscarTodos() {
        try {
            return Optional.of(rolRepositorio.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Rol> buscarPorId(Long id) {
        return rolRepositorio.findById(id);
    }

    @Override
    public Rol crear(Rol rol) {
        return rolRepositorio.save(rol);
    }

    @Override
    public Rol actualizar(Rol rol) {
        return rolRepositorio.save(rol);
    }

    @Override
    public void eliminar(Long id) {
        rolRepositorio.deleteById(id);
    }
}
