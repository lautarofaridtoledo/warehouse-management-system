package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.persistence;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, Long> {
	@Query("SELECT z.idZona FROM Zona z JOIN z.categoriasAdmitidas c WHERE c = :categoria")
	List<Long> findZonaIdsByCategoriaAdmitida(@Param("categoria") CategoriasProducto categoria);
}
