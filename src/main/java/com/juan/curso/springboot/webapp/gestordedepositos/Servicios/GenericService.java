package com.juan.curso.springboot.webapp.gestordedepositos.Servicios;

import java.util.List;
import java.util.Optional;

/**
 * Legacy: usar {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService}.
 */
@Deprecated(forRemoval = true)
public interface GenericService<T, L> {
    public Optional<List<T>> buscarTodos();
    public Optional<T> buscarPorId(Long id);
    public T crear(T t);
    public T actualizar(T t);
    public void eliminar(Long id);


}