package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioDTO {
    private Long idMovimientoInventario;
    private Long productoId;
    private Long ubicacionOrigenId;
    private Long ubicacionDestinoId;
    private int cantidad;
    private EstadoMovimientoInventario estado;
    private Date fecha;

    public MovimientoInventarioDTO(MovimientoInventario movimientoInventario) {
        this.idMovimientoInventario = movimientoInventario.getIdMovimientoInventario();
        this.productoId = movimientoInventario.getProductoId();
        this.ubicacionOrigenId = movimientoInventario.getUbicacionOrigenId();
        this.ubicacionDestinoId = movimientoInventario.getUbicacionDestinoId();
        this.cantidad = movimientoInventario.getCantidad();
        this.estado = movimientoInventario.getEstado();
        this.fecha = movimientoInventario.getFecha();
    }
}
