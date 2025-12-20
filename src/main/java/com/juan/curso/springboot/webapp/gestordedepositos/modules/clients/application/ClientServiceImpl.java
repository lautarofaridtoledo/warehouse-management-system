package com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Cliente;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.clients.persistence.ClienteRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.shared.application.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements GenericService<Cliente, Long>{
    private final ClienteRepositorio clienteRepositorio;

    @Autowired
    public ClientServiceImpl(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    public Optional<List<Cliente>> buscarTodos() {
        try {
            return Optional.of(clienteRepositorio.findAll());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) throws RecursoNoEncontradoException {
        try {
            return clienteRepositorio.findById(id);
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Cliente crear(Cliente cliente) {
        try {
            cliente = clienteRepositorio.save(cliente);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return cliente;
    }

    @Override
    public Cliente actualizar(Cliente cliente) throws RecursoNoEncontradoException {
        try {
            cliente = clienteRepositorio.save(cliente);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return cliente;
    }

    @Override
    public void eliminar(Long id) throws RecursoNoEncontradoException {
        try {
            clienteRepositorio.deleteById(id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
