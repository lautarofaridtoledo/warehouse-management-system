package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRecepcionCabeceraRequest {
    @NotNull
    private Long idProveedor;

    private EstadosDeOrden estado;
}
