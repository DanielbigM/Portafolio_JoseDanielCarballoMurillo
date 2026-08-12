/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.domain.Constante;
import TechShop.JoseDaniel.service.ConstanteService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/constante")
public class ConstanteController {

    private final ConstanteService constanteService;
    private final MessageSource messageSource;

    public ConstanteController(ConstanteService constanteService, MessageSource messageSource) {
        this.constanteService = constanteService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var lista = constanteService.getConstantes();
        model.addAttribute("constantes", lista);
        model.addAttribute("totalConstantes", lista.size());
        model.addAttribute("constante", new Constante());
        return "/constante/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Constante constante, RedirectAttributes redirectAttributes) {
        constanteService.save(constante);
        redirectAttributes.addFlashAttribute("msjSuccess", messageSource.getMessage("constante.guardado.exito", null, Locale.getDefault()));
        return "redirect:/constante/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idConstante, RedirectAttributes redirectAttributes) {
        try {
            constanteService.delete(idConstante);
            redirectAttributes.addFlashAttribute("msjSuccess", messageSource.getMessage("constante.eliminado.exito", null, Locale.getDefault()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msjError", e.getMessage());
        }
        return "redirect:/constante/listado";
    }

    @GetMapping("/modificar/{idConstante}")
    public String modificar(@PathVariable("idConstante") Integer idConstante, Model model) {
        Constante constante = constanteService.getConstante(idConstante);
        model.addAttribute("constante", constante);
        return "/constante/modifica";
    }
}