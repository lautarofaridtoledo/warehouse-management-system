package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolDTO {
    private Long id;
    private String nombre;

    public RolDTO(Rol rol) {
        this.id = rol.getId();
        this.nombre = rol.getNombre();
    }
}
