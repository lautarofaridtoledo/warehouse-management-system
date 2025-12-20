package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepositorio extends JpaRepository<Ubicacion, Long> {
    List<Ubicacion> findByCapacidadMaximaGreaterThanEqual(int cantidad);
        // NOTE: deshabilitado temporalmente por corte hard de Ubicacion->Zona.
        // Si se necesita filtrar por categorías admitidas, esto debe resolverse vía un
        // query service en el módulo de zonas o una tabla/materialización explícita.
        // @Query( ... JOIN zona ... )
        // List<Ubicacion> buscarUbicacionesPorCategoriaYEspacio(...)
}
