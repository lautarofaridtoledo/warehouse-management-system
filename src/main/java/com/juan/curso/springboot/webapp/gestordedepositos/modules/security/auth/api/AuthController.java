package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.AuthServicio;
import com.juan.curso.springboot.webapp.gestordedepositos.Config.PasswordEncoderConfig;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.CambioDeClaveDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/GestorDeDepositos")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    AuthServicio authServicio;
    @Autowired
    PasswordEncoderConfig passwordEncoderConfig;
    @Autowired
    UsuarioServiceImpl usuarioServiceImpl;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthController() {

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getContrasenia()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails usuario = (UserDetails) authentication.getPrincipal();
            String token = authServicio.generateToken(usuario.getUsername());

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setNombre(usuario.getUsername()); // es email ahora
            response.setRol(usuario.getAuthorities().iterator().next().getAuthority());

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email o contraseña incorrectos"));
        }
    }




    @PutMapping("/cambiarContrasenia")
    public ResponseEntity<?> cambiarContrasenia(@RequestBody CambioDeClaveDTO cambioDeClaveDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(cambioDeClaveDTO.getNombre(), cambioDeClaveDTO.getContrasenia()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails usuario =  authServicio.loadUserByUsername(cambioDeClaveDTO.getNombre());
        if(usuario.getPassword() == null){
            throw new UsernameNotFoundException("Usuario no encontrado: " + cambioDeClaveDTO.getNombre());

        }

        Usuario usuario1 = usuarioServiceImpl.getByNombreEquals(cambioDeClaveDTO.getNombre());
        if(usuario1 != null){
            usuario1.setContrasenia(passwordEncoder.encode(cambioDeClaveDTO.getNuevaContrasenia()));
        }
        usuario1 = usuarioServiceImpl.actualizar(usuario1);
        return new ResponseEntity<>(usuario1, HttpStatus.OK);
    }

}
