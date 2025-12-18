package com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import com.juan.curso.springboot.webapp.gestordedepositos.Repositorios.ProveedorRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements GenericService<Proveedor, Long> {

    @Autowired
    ProveedorRepositorio proveedorRepositorio;

    public ProveedorServiceImpl() {
    }

    @Override
    public Optional<List<Proveedor>> buscarTodos() {
        return Optional.of(proveedorRepositorio.findAll());
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) throws RecursoNoEncontradoException {
        return proveedorRepositorio.findById(id);
    }

    @Override
    public Proveedor crear(Proveedor proveedor) {
        proveedor = proveedorRepositorio.save(proveedor);
       
        return proveedor;
    }

    @Override
    public Proveedor actualizar(Proveedor proveedor) {
        if (!proveedorRepositorio.existsById(proveedor.getId_proveedor())) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + proveedor.getId_proveedor());
        }
        proveedor = proveedorRepositorio.save(proveedor);
        return proveedor;
    }

    @Override
    public void eliminar(Long id) {
        if (!proveedorRepositorio.existsById(id)) {
            throw new RecursoNoEncontradoException("Proveedor no encontrado con ID: " + id);
        }
        proveedorRepositorio.deleteById(id);
    }
}
