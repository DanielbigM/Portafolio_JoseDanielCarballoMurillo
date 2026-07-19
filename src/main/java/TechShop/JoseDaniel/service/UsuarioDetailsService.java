package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Rol;
import TechShop.JoseDaniel.domain.Usuario;
import TechShop.JoseDaniel.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsername(username);

        System.out.println("BUSCANDO USUARIO: " + username);

        if (usuario != null) {
            System.out.println("USUARIO ENCONTRADO: " + usuario.getUsername());
        }

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Collection<GrantedAuthority> roles = new ArrayList<>();

        // 1. Recorremos los roles del usuario y los agregamos a la colección
        for (Rol rol : usuario.getRoles()) {
            System.out.println("ROL ENCONTRADO: " + rol.getNombre());
            // Aquí usualmente se mapea el rol a un SimpleGrantedAuthority de Spring Security:
            roles.add(new SimpleGrantedAuthority(rol.getNombre()));
        } // <-- AQUÍ FALTABA CERRAR EL FOR

        // 2. Ahora que el FOR terminó de llenar la lista, retornamos el User con todos sus roles
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isActivo(),
                true,
                true,
                true,
                roles
        );
    }
}