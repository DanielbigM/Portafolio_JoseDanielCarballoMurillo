package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Rol;
import TechShop.JoseDaniel.domain.Usuario;
import TechShop.JoseDaniel.repository.UsuarioRepository;
import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameAndActivoTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        var roles = new ArrayList<GrantedAuthority>();
        if (usuario.getRoles() != null) {
            for (Rol rol : usuario.getRoles()) {
                roles.add(new SimpleGrantedAuthority(rol.getNombre()));
            }
        }

        return new User(usuario.getUsername(), usuario.getPassword(), roles);
    }
}