package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    @NotBlank
    @Column(nullable = false)
    private String nombre;
    @NotBlank
    @Column(nullable = false)
    private String contrasenia;
    @NotBlank
    @Column(nullable = false)
    private String apellido;
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "id_rol", nullable = false)
    private Long rolId;

}
