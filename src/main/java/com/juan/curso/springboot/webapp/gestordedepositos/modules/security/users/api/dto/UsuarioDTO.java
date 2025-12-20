package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String contrasenia;
    private String apellido;
    private String email;
    private Long idRol;

    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.email = usuario.getEmail();
        this.idRol = usuario.getRolId();
    }
}
