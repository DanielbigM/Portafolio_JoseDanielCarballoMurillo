package TechShop.JoseDaniel.repository;

import TechShop.JoseDaniel.domain.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    public List<Producto> findByActivoTrue();
    
    public List<Producto> findByCategoriaIdCategoria(Integer idCategoria);
    
    public List<Producto> findByPrecioBetweenOrderByPrecioAsc(
        BigDecimal precioInf,
        BigDecimal precioSup);
    
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC")
    public List<Producto> consultaJPQL(
        @Param("precioInf") BigDecimal precioInf,
        @Param("precioSup") BigDecimal precioSup);
    
    @Query(value = "SELECT * FROM producto p WHERE p.precio BETWEEN :precioInf AND :precioSup ORDER BY p.precio ASC",
       nativeQuery = true)
        public List<Producto> consultaSQL(
        @Param("precioInf") BigDecimal precioInf,
        @Param("precioSup") BigDecimal precioSup);
    
    
}

