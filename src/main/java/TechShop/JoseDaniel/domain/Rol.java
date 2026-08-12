package TechShop.JoseDaniel.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data // <--- Importante para generar getters/setters y aplicar las exclusiones
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @ToString.Exclude         
    @EqualsAndHashCode.Exclude 
    private Usuario usuario;
}