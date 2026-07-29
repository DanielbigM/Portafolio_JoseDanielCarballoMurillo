package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.domain.Categoria;
import TechShop.JoseDaniel.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    public CategoriaController(CategoriaService categoriaService,
                               MessageSource messageSource) {
        this.categoriaService = categoriaService;
        this.messageSource = messageSource;
    }

    /**
     * Muestra el listado de categorías.
     */
    @GetMapping("/listado")
    public String listado(Model model) {
        List<Categoria> categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        model.addAttribute("categoria", new Categoria());
        return "categoria/listado";
    }

    /**
     * Guarda una categoría nueva o actualiza una existente.
     * Recibe los datos del form + el archivo de imagen.
     */
    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria,
                          @RequestParam("imagenFile") MultipartFile imagenFile,
                          RedirectAttributes redirectAttributes) {
        try {
            categoriaService.save(categoria, imagenFile);
            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault())
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Error al guardar la categoría: " + e.getMessage()
            );
        }
        return "redirect:/categoria/listado";
    }

    /**
     * Elimina una categoría por su ID.
     */
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria,
                           RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";

        try {
            categoriaService.delete(idCategoria);
        } catch (IllegalArgumentException e) {
            // La categoría no existe
            titulo = "error";
            detalle = "categoria.error01";
        } catch (IllegalStateException e) {
            // Tiene datos asociados (no se puede borrar)
            titulo = "error";
            detalle = "categoria.error02";
        } catch (Exception e) {
            // Cualquier otro error
            titulo = "error";
            detalle = "categoria.error03";
        }

        redirectAttributes.addFlashAttribute(
                titulo,
                messageSource.getMessage(detalle, null, Locale.getDefault())
        );
        return "redirect:/categoria/listado";
    }

    /**
     * Muestra la página de edición de una categoría.
     */
    @GetMapping("/modificar/{idCategoria}")
    public String modificar(@PathVariable("idCategoria") Integer idCategoria,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(idCategoria);

        if (categoriaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    messageSource.getMessage("categoria.error01", null, Locale.getDefault())
            );
            return "redirect:/categoria/listado";
        }

        model.addAttribute("categoria", categoriaOpt.get());
        return "categoria/modifica";
    }
}