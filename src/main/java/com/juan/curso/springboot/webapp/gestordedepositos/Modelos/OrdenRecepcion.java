package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "orden_recepcion")
public class OrdenRecepcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_orden_recepcion")
    private Long idOrdenRecepcion;

    @Column(name = "id_proveedor", nullable = false)
    private Long proveedorId;

    private Date fecha;

    @Enumerated(EnumType.STRING)
    private EstadosDeOrden estado;

    @OneToMany(mappedBy = "ordenRecepcion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecepcion> detallesRecepcion;

}
