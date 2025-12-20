package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.PasswordEncoderConfig;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.api.dto.UsuarioDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.Excepciones.RecursoNoEncontradoException;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.application.RolServiceImpl;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/GestorDeDepositos/usuarios")
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final RolServiceImpl rolService;
    private final PasswordEncoderConfig passwordEncoderConfig;

    public UsuarioController(UsuarioServiceImpl usuarioService,
                            RolServiceImpl rolService,
                            PasswordEncoderConfig passwordEncoderConfig) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoderConfig = passwordEncoderConfig;
    }

    @PostMapping("/crear")
    @Operation(summary = "Este metodo crea un usuario")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        Rol rol = rolService.buscarPorId(usuarioDTO.getIdRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("El Rol seleccionado no existe."));

        Usuario usuario = new Usuario();
        String contraseniaEncriptada = passwordEncoderConfig.passwordEncoder().encode(usuarioDTO.getContrasenia());

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setContrasenia(contraseniaEncriptada);
        usuario.setApellido(usuarioDTO.getApellido());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setRol(rol);

        Usuario nuevoUsuario = usuarioService.crear(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(nuevoUsuario));
    }

    @PutMapping("actualizar")
    @Operation(summary = "Este metodo modifica un usuario (apellido, email, nombre y opcionalmente rol)")
    public ResponseEntity<UsuarioDTO> modificarUsuario(@RequestParam Long id, @RequestBody UsuarioDTO usuarioDTO) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio.");
        }

        Usuario u = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        u.setNombre(usuarioDTO.getNombre());
        u.setApellido(usuarioDTO.getApellido());
        u.setEmail(usuarioDTO.getEmail());

        if (usuarioDTO.getIdRol() != null) {
            Rol rol = rolService.buscarPorId(usuarioDTO.getIdRol())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El Rol especificado no existe."));
            u.setRol(rol);
        }

        Usuario usuarioActualizado = usuarioService.actualizar(u);

        return ResponseEntity.ok(new UsuarioDTO(usuarioActualizado));
    }

    @DeleteMapping("/eliminar")
    @Operation(summary = "Este metodo elimina un usuario")
    public ResponseEntity<String> eliminarUsuario(@RequestParam Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }

    @GetMapping("/buscarPorId")
    @Operation(summary = "Este metodo busca un usuario")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@RequestParam Long id) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));

        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @GetMapping("/todos")
    @Operation(summary = "Este metodo busca todos los usuarios")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuarios() {
        List<Usuario> usuarios = usuarioService.buscarTodos()
                .orElse(List.of());

        List<UsuarioDTO> dtos = usuarios.stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/buscarPorRol")
    @Operation(summary = "Este metodo busca usuarios por rol")
    public ResponseEntity<List<UsuarioDTO>> buscarPorRol(@RequestParam Long idRol) {
        Rol rol = rolService.buscarPorId(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado."));

        List<Usuario> usuarios = usuarioService.buscarPorRol(rol)
                .orElse(List.of());

        List<UsuarioDTO> dtos = usuarios.stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
