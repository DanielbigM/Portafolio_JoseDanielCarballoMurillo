package TechShop.JoseDaniel.repository;

import TechShop.JoseDaniel.domain.Constante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstanteRepository extends JpaRepository<Constante, Integer> {

    // Consulta derivada para buscar por nombre de atributo
    public Optional<Constante> findByAtributo(String atributo);
}