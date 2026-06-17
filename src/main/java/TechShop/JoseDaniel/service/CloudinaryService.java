package TechShop.JoseDaniel.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Sube una imagen a Cloudinary y devuelve la URL pública.
     *
     * @param archivo  el archivo de imagen del formulario
     * @param carpeta  carpeta en Cloudinary (ej: "categorias", "productos")
     * @return URL pública (https://...) de la imagen subida
     */
    public String subirImagen(MultipartFile archivo, String carpeta) throws IOException {
        // Nombre único para evitar colisiones
        String nombreUnico = "techshop_" + UUID.randomUUID().toString();

        // Subimos a Cloudinary
        Map resultado = cloudinary.uploader().upload(
                archivo.getBytes(),
                ObjectUtils.asMap(
                        "folder", carpeta,
                        "public_id", nombreUnico,
                        "resource_type", "image"
                )
        );

        // Devolvemos la URL segura (https)
        return (String) resultado.get("secure_url");
    }

    /**
     * Elimina una imagen de Cloudinary usando su URL.
     *
     * @param urlImagen la URL completa de la imagen en Cloudinary
     */
    public void eliminarImagen(String urlImagen) throws IOException {
        if (urlImagen == null || urlImagen.isBlank()) {
            return;
        }

        String publicId = extraerPublicId(urlImagen);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * Extrae el public_id de una URL de Cloudinary.
     * Ejemplo de URL:
     * https://res.cloudinary.com/CLOUD/image/upload/v123/categorias/techshop_xxx.jpg
     * → public_id = "categorias/techshop_xxx"
     */
    private String extraerPublicId(String url) {
        try {
            int idx = url.indexOf("/upload/");
            if (idx == -1) {
                return null;
            }
            String parte = url.substring(idx + 8);

            // Quitar la versión (v123456789/)
            if (parte.startsWith("v")) {
                parte = parte.substring(parte.indexOf("/") + 1);
            }

            // Quitar la extensión (.jpg, .png, etc)
            int dot = parte.lastIndexOf('.');
            if (dot != -1) {
                parte = parte.substring(0, dot);
            }

            return parte;
        } catch (Exception e) {
            return null;
        }
    }
}