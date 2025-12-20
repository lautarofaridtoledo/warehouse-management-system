package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
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
public class InventarioDTO {
    private Long id_inventario;
    private Producto producto;
    private Ubicacion ubicacion;
    private int cantidad;
    private Date fecha_actualizacion;

    public InventarioDTO(Inventario inventario) {
        this.id_inventario = inventario.getIdInventario();
        this.producto = inventario.getProducto();
        this.ubicacion = inventario.getUbicacion();
        this.cantidad = inventario.getCantidad();
        this.fecha_actualizacion = inventario.getFecha_actualizacion();
    }
}
