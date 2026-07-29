package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.domain.Usuario;
import TechShop.JoseDaniel.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public UsuarioController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String inicio(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuario", new Usuario()); // Objeto necesario para el modal "Agregar Usuario"
        return "usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, BindingResult bindingResult,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return usuario.getIdUsuario() == null ? "usuario/listado" : "usuario/modifica";
        }
        usuarioService.save(usuario, imagenFile, true);
        redirectAttributes.addFlashAttribute("mensaje",
                messageSource.getMessage("usuario.guardado", null, "Usuario guardado con éxito", Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("idUsuario") Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(idUsuario);
            redirectAttributes.addFlashAttribute("mensaje",
                    messageSource.getMessage("usuario.eliminado", null, "Usuario eliminado con éxito", Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("usuario.error01", null, "Error al eliminar el usuario", Locale.getDefault()));
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("usuario.error02", null, "No se puede eliminar el usuario", Locale.getDefault()));
        } catch (NoSuchMessageException e) {
            redirectAttributes.addFlashAttribute("error", "Error inesperado al procesar la solicitud");
        }
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable("idUsuario") Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no fue encontrado.");
            return "redirect:/usuario/listado";
        }
        Usuario usuario = usuarioOpt.get();
        usuario.setPassword("");
        model.addAttribute("usuario", usuario);
        return "usuario/modifica";
    }
}