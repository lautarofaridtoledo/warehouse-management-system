package com.juan.curso.springboot.webapp.gestordedepositos.modules.orders.recepcion.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRecepcionBulkRequest {
    private Long idOrdenRecepcion;

    @NotEmpty
    @Valid
    private List<DetalleRecepcionItemDTO> detalles;
}
