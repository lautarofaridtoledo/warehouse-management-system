package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.strategy;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;

import java.util.List;

public interface UbicacionSelectionStrategy {
    Ubicacion seleccionar(List<Ubicacion> candidatos);
}
