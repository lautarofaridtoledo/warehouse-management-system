package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenRecepcion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRecepcionDTO {

    private Long id_orden_recepcion;
    private Long proveedorId;
    private Date fecha;
    private EstadosDeOrden estado;
    private List<DetalleRecepcionDTO> detalleRecepcionDTOList;

    public OrdenRecepcionDTO(OrdenRecepcion ordenRecepcion) {
        this.id_orden_recepcion = ordenRecepcion.getIdOrdenRecepcion();
        this.estado = ordenRecepcion.getEstado();
        this.fecha = ordenRecepcion.getFecha();
        this.proveedorId = ordenRecepcion.getProveedorId();
        List<DetalleRecepcionDTO> detalles = ordenRecepcion.getDetallesRecepcion() == null
                ? Collections.emptyList()
                : ordenRecepcion.getDetallesRecepcion().stream().map(DetalleRecepcionDTO::new).collect(Collectors.toList());
        this.detalleRecepcionDTOList = detalles;
    }
}
