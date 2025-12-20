package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionItemDTO {

    private Long idOrdenRecepcion;

    private Producto producto;

    private String codigoSku;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
