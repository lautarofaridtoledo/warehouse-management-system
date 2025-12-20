package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Zona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ZonaDTO {
    private Long idZona;
    private String nombre;
    private String descripcion;
    private List<CategoriasProducto> categoriasAdmitidas;

    public ZonaDTO(Zona zona) {
        this.idZona = zona.getIdZona();
        this.nombre = zona.getNombre();
        this.descripcion = zona.getDescripcion();
        this.categoriasAdmitidas = zona.getCategoriasAdmitidas();
    }
}
