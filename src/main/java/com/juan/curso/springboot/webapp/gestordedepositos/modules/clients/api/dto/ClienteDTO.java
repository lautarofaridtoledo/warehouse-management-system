package com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {
    private Long idCliente;
    private String nombre;
    private String telefono;
    private String email;

    public ClienteDTO(Cliente cliente) {
        this.idCliente = cliente.getIdCliente();
        this.nombre = cliente.getNombre();
        this.telefono = cliente.getTelefono();
        this.email = cliente.getEmail();
    }
}
