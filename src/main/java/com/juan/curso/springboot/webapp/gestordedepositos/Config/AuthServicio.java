package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.persistence.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.application.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthServicio  implements UserDetailsService {
    @Autowired
    UsuarioServiceImpl usuarioRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    RolRepositorio rolRepositorio;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.getByEmailEquals(email);

        if(usuario == null){
            throw new UsernameNotFoundException("Usuario no encontrado: " + email);
        }

        Rol rol = usuario.getRol();

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getContrasenia())
                .authorities(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                .build();
    }

    public String generateToken(String email){
        return jwtUtil.generateToken(email);
    }
}