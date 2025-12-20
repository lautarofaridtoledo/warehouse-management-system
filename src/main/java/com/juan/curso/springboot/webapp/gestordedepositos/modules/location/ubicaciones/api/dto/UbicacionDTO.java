package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.ubicaciones.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionDTO {
    private Long idUbicacion;
    private String codigo;
    private Zona zona;
    private int capacidadMaxima;
    private int ocupadoActual;

    public UbicacionDTO(Ubicacion ubicacion) {
        this.idUbicacion = ubicacion.getIdUbicacion();
        this.codigo = ubicacion.getCodigo();
        this.zona = ubicacion.getZona();
        this.capacidadMaxima = ubicacion.getCapacidadMaxima();
        this.ocupadoActual = ubicacion.getOcupadoActual();
    }
}
