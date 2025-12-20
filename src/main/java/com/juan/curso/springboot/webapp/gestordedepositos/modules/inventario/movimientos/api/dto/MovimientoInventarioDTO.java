package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.movimientos.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.MovimientoInventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Ubicacion;
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
    private Producto producto;
    private Ubicacion ubicacionOrigen;
    private Ubicacion ubicacionDestino;
    private int cantidad;
    private EstadoMovimientoInventario estado;
    private Date fecha;

    public MovimientoInventarioDTO(MovimientoInventario movimientoInventario) {
        this.idMovimientoInventario = movimientoInventario.getIdMovimientoInventario();
        this.producto = movimientoInventario.getProducto();
        this.ubicacionOrigen = movimientoInventario.getUbicacionOrigen();
        this.ubicacionDestino = movimientoInventario.getUbicacionDestino();
        this.cantidad = movimientoInventario.getCantidad();
        this.estado = movimientoInventario.getEstado();
        this.fecha = movimientoInventario.getFecha();
    }
}
