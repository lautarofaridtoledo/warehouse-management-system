package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleRecepcion;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionDTO {
    private Long idDetalleRecepcion;
    @JsonIgnore
    private OrdenRecepcion orden;
    private Long productoId;
    private int cantidad;

    private Long idOrdenRecepcion;
    private String codigoSku;

    public DetalleRecepcionDTO(DetalleRecepcion detalle) {
        this.idDetalleRecepcion = detalle.getIdDetalleRecepcion();
        this.orden = detalle.getOrdenRecepcion();
        this.idOrdenRecepcion = detalle.getOrdenRecepcion() != null ? detalle.getOrdenRecepcion().getIdOrdenRecepcion() : null;
        this.productoId = detalle.getProductoId();
        this.cantidad = detalle.getCantidad();
    }
}
