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

@RestController
@RequestMapping("GestorDeDepositos/producto")
public class ProductoController {

    private final ProductoServiceImpl productoServiceImpl;

    @Autowired
    public ProductoController(ProductoServiceImpl productoServiceImpl) {
        this.productoServiceImpl = productoServiceImpl;
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los productos")
    public ResponseEntity<List<ProductoDTO>> buscarTodos() {
        List<ProductoDTO> productos = productoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron productos"))
                .stream()
                .filter(p -> p.getIsDeleted().equals("N"))
                .map(ProductoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Este metodo busca un producto por su id")
    public ResponseEntity<ProductoDTO> buscar(@RequestParam Long id) {
        Producto producto = productoServiceImpl.buscarPorId(id)
                .filter(p -> p.getIsDeleted().equals("N"))
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        
        return ResponseEntity.ok(new ProductoDTO(producto));
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un nuevo producto")
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDTO) {
        Producto productoEntity = toEntity(productoDTO);
        productoEntity.setFecha_creacion(Calendar.getInstance().getTime());
        productoEntity = productoServiceImpl.crear(productoEntity);
        
        return new ResponseEntity<>(new ProductoDTO(productoEntity), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar")
    @Operation(summary = "Este metodo actualiza un producto")
    public ResponseEntity<ProductoDTO> actualizar(@RequestParam Long id, @RequestBody ProductoDTO dto) {
        // Verificar que existe
        productoServiceImpl.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        
        Producto producto = toEntity(dto);
        producto.setIdProducto(id);
        producto.setFecha_creacion(dto.getFecha_creacion() != null ? dto.getFecha_creacion() : Calendar.getInstance().getTime());
        producto = productoServiceImpl.actualizar(producto);
        
        return ResponseEntity.ok(new ProductoDTO(producto));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un producto")
    public ResponseEntity<String> eliminar(@RequestParam Long id) {
        productoServiceImpl.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));
        
        productoServiceImpl.eliminar(id);
        return ResponseEntity.ok("Producto eliminado");
    }

    @GetMapping("/buscarPorCodigoSku")
    @Operation(summary = "Este metodo busca producto por su codigo sku")
    public ResponseEntity<ProductoDTO> buscarPorCodigoSKU(@RequestParam String codigo) {
        Producto producto = productoServiceImpl.buscarPorCodigoSKU(codigo);

        if (producto == null || producto.getIsDeleted().equals("S")) {
            throw new RecursoNoEncontradoException("Producto no encontrado");
        }

        return ResponseEntity.ok(new ProductoDTO(producto));
    }

    @GetMapping("/buscarPorNombreOCodigo")
    @Operation(summary = "Busca productos por nombre o código SKU")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombreOCodigo(@RequestParam String valor) {
        List<ProductoDTO> productos = productoServiceImpl.buscarTodos()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontraron productos"))
                .stream()
                .filter(p -> p.getIsDeleted().equals("N") &&
                        (p.getNombre().toLowerCase().contains(valor.toLowerCase()) ||
                                p.getCodigoSku().toLowerCase().contains(valor.toLowerCase())))
                .map(ProductoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productos);
    }

    // ============ Método auxiliar ============

    private Producto toEntity(ProductoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setCategoria(dto.getCategoria());
        producto.setCodigoSku(dto.getCodigoSku());
        producto.setUnidad_medida(dto.getUnidad_medida());
        producto.setFecha_creacion(dto.getFecha_creacion());
        producto.setIsDeleted("N");
        return producto;
    }
}