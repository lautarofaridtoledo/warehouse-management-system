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
    @Query("SELECT u FROM Ubicacion u " +
            "JOIN u.zona z " +
            "JOIN z.categoriasAdmitidas c " +
            "WHERE c = :categoria " +
            "AND (u.capacidadMaxima - u.ocupadoActual) >= :cantidad " +  // <-- AQUI ESTA EL CAMBIO
            "ORDER BY (u.capacidadMaxima - u.ocupadoActual) ASC") // Orden ASC para 'Best Fit' (llenar huecos pequeños primero)
    List<Ubicacion> buscarUbicacionesPorCategoriaYEspacio(
            @Param("categoria") CategoriasProducto categoria,
            @Param("cantidad") int cantidad
    );
}
