package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Categoria;
import TechShop.JoseDaniel.repository.CategoriaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CloudinaryService cloudinaryService;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            CloudinaryService cloudinaryService) {
        this.categoriaRepository = categoriaRepository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Obtiene todas las categorías o solo las activas.
     */
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        }
        return categoriaRepository.findAll();
    }

    /**
     * Obtiene una categoría por su ID (para editar).
     */
    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    /**
     * Guarda una categoría. Si viene una imagen, la sube a Cloudinary
     * y guarda la URL en el campo rutaImagen.
     */
    @Transactional
    public void save(Categoria categoria, MultipartFile imagenFile) {
        // 1. Primero guardamos la categoría para obtener su ID
        categoria = categoriaRepository.save(categoria);

        // 2. Si el usuario subió una imagen, la mandamos a Cloudinary
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                // Si ya tenía una imagen vieja, la eliminamos de Cloudinary
                if (categoria.getRutaImagen() != null && !categoria.getRutaImagen().isBlank()) {
                    cloudinaryService.eliminarImagen(categoria.getRutaImagen());
                }

                // Subimos la nueva imagen
                String rutaImagen = cloudinaryService.subirImagen(imagenFile, "categoria");
                categoria.setRutaImagen(rutaImagen);

                // Guardamos otra vez para actualizar la rutaImagen
                categoriaRepository.save(categoria);
            } catch (IOException e) {
                // Si falla la subida, al menos la categoría queda guardada sin imagen
                System.err.println("Error al subir imagen a Cloudinary: " + e.getMessage());
            }
        }
    }

    /**
     * Elimina una categoría por su ID.
     * También elimina la imagen de Cloudinary si existe.
     */
    @Transactional
    public void delete(Integer idCategoria) {
        // Verifica si la categoría existe antes de intentar eliminarla
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException("La categoría con ID " + idCategoria + " no existe.");
        }

        try {
            // Antes de borrar, intentamos eliminar la imagen de Cloudinary
            Optional<Categoria> catOpt = categoriaRepository.findById(idCategoria);
            if (catOpt.isPresent() && catOpt.get().getRutaImagen() != null) {
                try {
                    cloudinaryService.eliminarImagen(catOpt.get().getRutaImagen());
                } catch (IOException e) {
                    System.err.println("No se pudo eliminar la imagen de Cloudinary: " + e.getMessage());
                }
            }

            categoriaRepository.deleteById(idCategoria);
        } catch (DataIntegrityViolationException e) {
            // Si hay productos relacionados, MySQL no deja borrar
            throw new IllegalStateException("No se puede eliminar la categoría. Tiene datos asociados.", e);
        }
    }
}
