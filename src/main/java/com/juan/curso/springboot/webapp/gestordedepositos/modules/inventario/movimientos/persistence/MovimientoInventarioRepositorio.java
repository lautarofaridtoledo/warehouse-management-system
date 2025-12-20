package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.persistence;


import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoInventarioRepositorio extends JpaRepository<MovimientoInventario, Long> {
}
