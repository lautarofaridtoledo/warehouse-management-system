package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
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
    private Producto producto;
    private int cantidad;

    public DetalleDespachoDTO(DetalleDespacho detalle) {
        this.idDetalleDespacho = detalle.getIdDetalleDespacho();
        this.ordenDespacho = detalle.getOrdenDespacho();
        this.producto = detalle.getProducto();
        this.cantidad = detalle.getCantidad();
    }
}
