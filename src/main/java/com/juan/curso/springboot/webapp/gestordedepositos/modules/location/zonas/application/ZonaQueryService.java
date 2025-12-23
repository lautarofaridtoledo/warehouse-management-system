package com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Enums.CategoriasProducto;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.location.zonas.persistence.ZonaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZonaQueryService {

    private final ZonaRepositorio zonaRepositorio;

    public ZonaQueryService(ZonaRepositorio zonaRepositorio) {
        this.zonaRepositorio = zonaRepositorio;
    }

    public List<Long> findZonaIdsByCategoriaAdmitida(CategoriasProducto categoria) {
        return zonaRepositorio.findZonaIdsByCategoriaAdmitida(categoria);
    }
}
