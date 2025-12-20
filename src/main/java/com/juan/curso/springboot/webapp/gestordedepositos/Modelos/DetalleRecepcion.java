package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "detalle_recepcion")
public class DetalleRecepcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_detalle_recepcion")
    private Long idDetalleRecepcion;

    @ManyToOne
    @JoinColumn(name = "id_orden_recepcion")
    @JsonIgnore
    private OrdenRecepcion ordenRecepcion;

    @Column(name = "id_producto", nullable = false)
    private Long productoId;

    @NotNull
    private int cantidad;


    public DetalleRecepcion(Long idProducto, Double cantidad) {
    }

    public DetalleRecepcion(OrdenRecepcion ordenRecepcion, Long idProducto, Double cantidad) {
    }
}
