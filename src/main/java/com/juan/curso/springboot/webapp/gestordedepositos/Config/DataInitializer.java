package com.juan.curso.springboot.webapp.gestordedepositos.Config;

import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Rol;
import com.juan.curso.springboot.webapp.gestordedepositos.Modelos.Usuario;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.roles.persistence.RolRepositorio;
import com.juan.curso.springboot.webapp.gestordedepositos.modules.security.users.persistence.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private RolRepositorio rolRepositorio;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        if (usuarioRepositorio.count() == 0) {
            System.out.println("=== INICIALIZANDO DATOS DEL SISTEMA ===");
            createRolesIfNotExist();
            createDefaultAdminUser();
            System.out.println("=== DATOS INICIALES CREADOS EXITOSAMENTE ===");
        } else {
            System.out.println("=== SISTEMA YA INICIALIZADO ===");
        }
    }

    private void createRolesIfNotExist() {

        if (rolRepositorio.findByNombre("ADMIN") == null) {
            Rol adminRole = new Rol();
            adminRole.setNombre("ADMIN");
            rolRepositorio.save(adminRole);
            System.out.println("Rol ADMIN creado");
        }

        if (rolRepositorio.findByNombre("OPERATIVO") == null) {
            Rol operativoRole = new Rol();
            operativoRole.setNombre("OPERATIVO");
            rolRepositorio.save(operativoRole);
            System.out.println("Rol OPERATIVO creado");
        }
    }

    private void createDefaultAdminUser() {
        Rol adminRole = rolRepositorio.findByNombre("ADMIN");
        
        if (adminRole != null) {
            Usuario adminUser = new Usuario();
            adminUser.setNombre("admin");
            adminUser.setContrasenia(passwordEncoder.encode("admin123"));
            adminUser.setApellido("Administrador");
            adminUser.setEmail("admin@gestordepositos.com");
            adminUser.setRol(adminRole);
            
            usuarioRepositorio.save(adminUser);
            
            System.out.println("=== USUARIO ADMINISTRADOR CREADO ===");
            System.out.println("Usuario: admin");
            System.out.println("Contraseña: admin123");
            System.out.println("Email: admin@gestordepositos.com");
            System.out.println("IMPORTANTE: Cambiar la contraseña después del primer login");
        }
    }
}