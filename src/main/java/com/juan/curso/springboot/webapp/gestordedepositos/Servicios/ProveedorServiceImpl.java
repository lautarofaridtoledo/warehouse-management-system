package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Proveedor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Deprecated(forRemoval = true)
public class ProveedorServiceImpl implements GenericService<Proveedor, Long> {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.application.ProveedorServiceImpl delegate;

    @Autowired
    public ProveedorServiceImpl( com.juan.curso.springboot.webapp.gestordedepositos.modules.proveedores.application.ProveedorServiceImpl delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<List<Proveedor>> buscarTodos() {
        return this.delegate.buscarTodos();
    }

    @Override
    public Optional<Proveedor> buscarPorId(Long id) throws RecursoNoEncontradoException {
       return this.delegate.buscarPorId(id);
    }

    @Override
    public Proveedor crear(Proveedor proveedor) {
       return this.delegate.crear(proveedor);
    }

    @Override
    public Proveedor actualizar(Proveedor proveedor) {
        return this.delegate.actualizar(proveedor);
    }

    @Override
    public void eliminar(Long id) {
        this.delegate.eliminar(id);
    }
}
