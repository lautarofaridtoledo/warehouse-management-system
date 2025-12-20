package com.juan.curso.springboot.webapp.gestordedepositos.Modelos;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.EstadosDeOrden;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orden_despacho")
public class OrdenDespacho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_despacho")
    private Long IdOrdenDespacho;

    @Column(nullable = false)
    private Date fechaDespacho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadosDeOrden estado;

    @Column(name = "id_cliente", nullable = false)
    private Long clienteId;

    @OneToMany(mappedBy = "ordenDespacho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name="detalle_despacho")
    private List<DetalleDespacho> detalleDespacho = new ArrayList<>();
}