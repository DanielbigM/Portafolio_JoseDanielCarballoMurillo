package TechShop.JoseDaniel.service;

import TechShop.JoseDaniel.domain.Producto;
import TechShop.JoseDaniel.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CloudinaryService cloudinaryService;

    public ProductoService(ProductoRepository productoRepository,
                           CloudinaryService cloudinaryService) {
        this.productoRepository = productoRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) {
            return productoRepository.findByActivoTrue();
        }
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                if (producto.getRutaImagen() != null && !producto.getRutaImagen().isBlank()) {
                    cloudinaryService.eliminarImagen(producto.getRutaImagen());
                }
                String rutaImagen = cloudinaryService.subirImagen(imagenFile, "producto");
                producto.setRutaImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {
                System.err.println("Error al subir imagen: " + e.getMessage());
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("El producto con ID " + idProducto + " no existe.");
        }

        try {
            Optional<Producto> prodOpt = productoRepository.findById(idProducto);
            if (prodOpt.isPresent() && prodOpt.get().getRutaImagen() != null) {
                try {
                    cloudinaryService.eliminarImagen(prodOpt.get().getRutaImagen());
                } catch (IOException e) {
                    System.err.println("No se pudo eliminar la imagen: " + e.getMessage());
                }
            }
            productoRepository.deleteById(idProducto);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto. Tiene datos asociados.", e);
        }
    }
}