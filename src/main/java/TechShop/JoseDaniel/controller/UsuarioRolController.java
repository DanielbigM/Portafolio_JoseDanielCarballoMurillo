package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.domain.Usuario;
import TechShop.JoseDaniel.service.UsuarioService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario_rol")
public class UsuarioRolController {

    private final UsuarioService usuarioService;

    public UsuarioRolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento() {
        return "usuario_rol/mantenimiento";
    }

    @GetMapping("/buscar")
    public String buscarUsuario(@RequestParam("username") String username, Model model) {
        // 1. Corregido: se usa getUsuarioPorUsername en lugar de findByUsername
        Usuario usuario = usuarioService.getUsuarioPorUsername(username).orElse(null);

        if (usuario == null) {
            model.addAttribute("error", "El usuario '" + username + "' no fue encontrado.");
            return "usuario_rol/mantenimiento";
        }

        List<String> todosRolesNombres = usuarioService.getRolesNombres();

        // 2. Corregido: se usa asignado.getNombre() en lugar de asignado.getRol()
        List<String> rolesDisponibles = todosRolesNombres.stream()
            .filter(rolNombre -> usuario.getRoles().stream()
                .noneMatch(asignado -> rolNombre.equals(asignado.getNombre())))
            .toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("rolesDisponibles", rolesDisponibles);

        return "usuario_rol/mantenimiento";
    }

    @PostMapping("/agregar")
    public String agregarRol(@RequestParam("idUsuario") Long idUsuario, 
                             @RequestParam("nombreRol") String nombreRol, 
                             RedirectAttributes redirectAttributes) {
        // 3. Corregido: se usa getUsuario(Integer) en lugar de findById(Long)
        Usuario usuario = usuarioService.getUsuario(idUsuario.intValue()).orElse(null);
        if (usuario != null) {
            usuarioService.asignarRol(idUsuario, nombreRol);
            redirectAttributes.addAttribute("username", usuario.getUsername());
        }
        return "redirect:/usuario_rol/buscar";
    }

    @PostMapping("/eliminar")
    public String eliminarRol(@RequestParam("idUsuario") Long idUsuario, 
                              @RequestParam("idRol") Long idRol, 
                              RedirectAttributes redirectAttributes) {
        // 4. Corregido: se usa getUsuario(Integer) en lugar de findById(Long)
        Usuario usuario = usuarioService.getUsuario(idUsuario.intValue()).orElse(null);
        if (usuario != null) {
            usuarioService.eliminarRol(idUsuario, idRol);
            redirectAttributes.addAttribute("username", usuario.getUsername());
        }
        return "redirect:/usuario_rol/buscar";
    }
}