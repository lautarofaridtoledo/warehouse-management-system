package com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico para operaciones CRUD simples.
 *
 * Nota: este tipo vivía en el paquete legacy {@code Servicios}.
 * Se movió a {@code modules/shared} para que los módulos no dependan del legacy.
 */
public interface GenericService<T, L> {

    Optional<List<T>> buscarTodos();

    Optional<T> buscarPorId(Long id);

    T crear(T t);

    T actualizar(T t);

    void eliminar(Long id);
}
