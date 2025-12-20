package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadoMovimientoInventario;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimientoInventario;

    @Column(name = "id_producto", nullable = false)
    private Long productoId;

    @Column(name = "id_ubicacion_origen")
    private Long ubicacionOrigenId;

    @Column(name = "id_ubicacion_destino")
    private Long ubicacionDestinoId;

    private int cantidad;

    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadoMovimientoInventario estado;
}