package TechShop.JoseDaniel.repository;

import TechShop.JoseDaniel.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    public Optional<Usuario> findByUsernameAndActivoTrue(String username);
    
    public List<Usuario> findByActivoTrue();
    
    public Optional<Usuario> findByUsername(String username);
    
    public Optional<Usuario> findByUsernameAndPassword(String username, String password);
    
    public Optional<Usuario> findByUsernameOrCorreo(String username, String correo);
    
    public boolean existsByUsernameOrCorreo(String username, String correo);
}