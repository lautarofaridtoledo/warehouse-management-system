package com.juan.curso.springboot.webapp.gestordedepositos.Controladores;

import com.juan.curso.springboot.webapp.gestordedepositos.Dtos.ProductoDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Producto;
import com.juan.curso.springboot.webapp.gestordedepositos.Servicios.ProductoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper de compatibilidad.
 *
 * Migración estructural: el controller real vive en
 * {@link com.juan.curso.springboot.webapp.gestordedepositos.modules.products.api.ProductoController}.
 *
 * Nota: no se anota con @RestController para evitar endpoints duplicados.
 */
@Deprecated(forRemoval = true)
public class ProductoController {

    private final com.juan.curso.springboot.webapp.gestordedepositos.modules.products.api.ProductoController delegate;

    @Autowired
    public ProductoController(
            com.juan.curso.springboot.webapp.gestordedepositos.modules.products.api.ProductoController delegate) {
        this.delegate = delegate;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los productos")
    public ResponseEntity<List<ProductoDTO>> buscarTodos() {
        return delegate.buscarTodos();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un producto por su id")
    public ResponseEntity<ProductoDTO> buscar(@RequestParam Long id) {
        return delegate.buscar(id);
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo producto")
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDTO) {
        return delegate.crearProducto(productoDTO);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un producto")
    public ResponseEntity<ProductoDTO> actualizar(@RequestParam Long id, @RequestBody ProductoDTO dto) {
        return delegate.actualizar(id, dto);
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un producto")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        return delegate.eliminar(id);
    }

    @GetMapping("/buscarPorCodigoSku")
    @Operation(summary = "Este metodo busca producto por su codigo sku")
    public ResponseEntity<ProductoDTO> buscarPorCodigoSKU(@RequestParam String codigo) {
        return delegate.buscarPorCodigoSKU(codigo);
    }

    @GetMapping("/buscarPorNombreOCodigo")
    @Operation(summary = "Busca productos por nombre o código SKU")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombreOCodigo(@RequestParam String valor) {
        return delegate.buscarPorNombreOCodigo(valor);
    }
}