package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDespachoDTO {
    private Long idDetalleDespacho;
    private OrdenDespacho ordenDespacho;
    private Long productoId;
    private int cantidad;

    public DetalleDespachoDTO(DetalleDespacho detalle) {
        this.idDetalleDespacho = detalle.getIdDetalleDespacho();
        this.ordenDespacho = detalle.getOrdenDespacho();
        this.productoId = detalle.getProductoId();
        this.cantidad = detalle.getCantidad();
    }
}
