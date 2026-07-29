package TechShop.JoseDaniel.repository;

import TechShop.JoseDaniel.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    
    // Cambiado de findByRol a findByNombre
    public Optional<Rol> findByNombre(String nombre);
}