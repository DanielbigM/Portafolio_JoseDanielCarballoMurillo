package TechShop.JoseDaniel.repository;

import TechShop.JoseDaniel.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    // Buscar todas las categorías activas
    public List<Categoria> findByActivoTrue();
}
