package com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.CapacidadExcedida;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Inventario;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.JwtUtil;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.api.dto.InventarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioApplicationService;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.inventario.application.InventarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Calendar;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventarioControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventarioApplicationService inventarioApplicationService;

    @MockBean
    private InventarioServiceImpl inventarioService;

    // En @WebMvcTest a veces se activa el wiring de security/jwt; mockeamos estos beans para que el slice levante.
    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;



    @Test
    void crear_happyPath_returns201AndBody() throws Exception {
        InventarioDTO request = new InventarioDTO();
        request.setProductoId(10L);
        request.setUbicacionId(20L);
        request.setCantidad(5);

        Inventario created = new Inventario();
        created.setIdInventario(1L);
        created.setProductoId(10L);
        created.setUbicacionId(20L);
        created.setCantidad(5);
        created.setFecha_actualizacion(Calendar.getInstance().getTime());

        when(inventarioApplicationService.crear(any(InventarioDTO.class))).thenReturn(created);

        mockMvc.perform(post("/GestorDeDepositos/inventario/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(10))
                .andExpect(jsonPath("$.ubicacionId").value(20))
                .andExpect(jsonPath("$.cantidad").value(5));
    }

    @Test
    void crear_whenCapacidadExcedida_returns4xx() throws Exception {
        InventarioDTO request = new InventarioDTO();
        request.setProductoId(10L);
        request.setUbicacionId(20L);
        request.setCantidad(5000);

        // Dejamos que el handler global decida el status exacto (depende de tu GlobalExceptionHandler).
        when(inventarioApplicationService.crear(any(InventarioDTO.class)))
                .thenThrow(new CapacidadExcedida("La ubicación no tiene capacidad suficiente"));

        mockMvc.perform(post("/GestorDeDepositos/inventario/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
