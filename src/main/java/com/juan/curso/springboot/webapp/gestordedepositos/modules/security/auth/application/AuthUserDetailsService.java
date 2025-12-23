package com.juan.curso.springboot.webapp.gestordedepositos.modules.security.auth.application;

import com.juan.curso.springboot.webapp.gestordedepositos.Config.JwtUtil;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.persistence.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UsuarioServiceImpl usuarioService;
    private final JwtUtil jwtUtil;
    private final RolRepositorio rolRepositorio;

    public AuthUserDetailsService(UsuarioServiceImpl usuarioService, JwtUtil jwtUtil, RolRepositorio rolRepositorio) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.rolRepositorio = rolRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.getByEmailEquals(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        Long rolId = usuario.getRolId();
        if (rolId == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + email);
        }

        Rol rol = rolRepositorio.findById(rolId)
                .orElseThrow(() -> new UsernameNotFoundException("Rol no encontrado para usuario: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getContrasenia())
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .build();
    }

    public String generateToken(String email) {
        return jwtUtil.generateToken(email);
    }
}
