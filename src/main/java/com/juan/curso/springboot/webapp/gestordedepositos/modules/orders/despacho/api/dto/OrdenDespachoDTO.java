package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.despacho.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.DetalleDespacho;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.OrdenDespacho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDespachoDTO {
    private Long idOrdenDespacho;
    private Date fechaDespacho;
    private EstadosDeOrden estado;
    private Cliente cliente;
    private List<DetalleDespacho> detalle_despacho;

    public OrdenDespachoDTO(OrdenDespacho ordenDespacho) {
        this.idOrdenDespacho = ordenDespacho.getIdOrdenDespacho();
        this.fechaDespacho = ordenDespacho.getFechaDespacho();
        this.estado = ordenDespacho.getEstado();
        this.cliente = ordenDespacho.getCliente();
        this.detalle_despacho = ordenDespacho.getDetalleDespacho();
    }
}
