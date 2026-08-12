package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Constante;
import TechShop.JoseDaniel.repository.ConstanteRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConstanteService {

    private final ConstanteRepository constanteRepository;

    public ConstanteService(ConstanteRepository constanteRepository) {
        this.constanteRepository = constanteRepository;
    }

    @Transactional(readOnly = true)
    public List<Constante> getConstantes() {
        return constanteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Constante getConstante(Integer idConstante) {
        return constanteRepository.findById(idConstante).orElseThrow(
            () -> new NoSuchElementException("Constante no encontrada con ID: " + idConstante)
        );
    }

    @Transactional(readOnly = true)
    public Optional<Constante> findByAtributo(String atributo) {
        return constanteRepository.findByAtributo(atributo);
    }

    @Transactional
    public void save(Constante constante) {
        if (constante.getIdConstante() == null) {
            constante.setFechaCreacion(LocalDateTime.now());
        } else {
            constante.setFechaModificacion(LocalDateTime.now());
        }
        constanteRepository.save(constante);
    }

    @Transactional
    public void delete(Integer idConstante) {
        if (!constanteRepository.existsById(idConstante)) {
            throw new IllegalArgumentException("La constante con ID " + idConstante + " no existe.");
        }
        try {
            constanteRepository.deleteById(idConstante);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la constante debido a restricciones de integridad.");
        }
    }
}