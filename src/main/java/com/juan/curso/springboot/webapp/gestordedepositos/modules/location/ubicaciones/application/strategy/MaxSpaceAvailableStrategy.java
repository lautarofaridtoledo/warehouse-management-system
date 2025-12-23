package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.application.strategy;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class MaxSpaceAvailableStrategy implements UbicacionSelectionStrategy {
    @Override
    public Ubicacion seleccionar(List<Ubicacion> candidatos) {
        if (candidatos == null || candidatos.isEmpty()) {
            throw new IllegalArgumentException("La lista de candidatos no puede ser vacía");
        }

        return candidatos.stream()
                .max(Comparator.comparingInt(u -> u.getCapacidadMaxima() - u.getOcupadoActual()))
                .orElseThrow(() -> new IllegalStateException("No se pudo seleccionar una ubicación"));
    }
}
