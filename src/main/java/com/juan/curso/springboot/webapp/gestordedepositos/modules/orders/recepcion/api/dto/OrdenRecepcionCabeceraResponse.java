package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRecepcionCabeceraResponse {
    private Long idOrdenRecepcion;

    private EstadosDeOrden estado;

    private Date fecha;
}
