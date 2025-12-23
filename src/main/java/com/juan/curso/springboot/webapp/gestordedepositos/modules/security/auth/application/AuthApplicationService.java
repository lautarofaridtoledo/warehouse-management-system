package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.CambioDeClaveDTO;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginRequest;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.api.dto.LoginResponse;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthApplicationService {

    private final AuthenticationManager authenticationManager;
    private final AuthUserDetailsService authUserDetailsService;
    private final UsuarioServiceImpl usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(AuthenticationManager authenticationManager,
                                  AuthUserDetailsService authUserDetailsService,
                                  UsuarioServiceImpl usuarioService,
                                  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.authUserDetailsService = authUserDetailsService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getContrasenia()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails principal = (UserDetails) authentication.getPrincipal();
    String token = authUserDetailsService.generateToken(principal.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setNombre(principal.getUsername()); // hoy es email
        response.setRol(principal.getAuthorities().iterator().next().getAuthority());
        return response;
    }

    public void cambiarContrasenia(CambioDeClaveDTO dto) {
        // Validar credenciales actuales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getContrasenia())
        );

        UserDetails userDetails = authUserDetailsService.loadUserByUsername(dto.getEmail());
        if (userDetails.getPassword() == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + dto.getEmail());
        }

        Usuario usuario = usuarioService.getByEmailEquals(dto.getEmail());
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + dto.getEmail());
        }

        usuario.setContrasenia(passwordEncoder.encode(dto.getNuevaContrasenia()));
        usuarioService.actualizar(usuario);
    }
}
