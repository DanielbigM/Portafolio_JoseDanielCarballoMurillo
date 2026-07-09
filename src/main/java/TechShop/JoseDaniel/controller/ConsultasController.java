package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.service.CategoriaService;
import TechShop.JoseDaniel.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@Controller
@RequestMapping("/consultas")
public class ConsultasController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public ConsultasController(CategoriaService categoriaService,
                               ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/categoria/{idCategoria}")
    public String consultaPorCategoria(@PathVariable Integer idCategoria,Model model) {

    model.addAttribute(
            "categorias",
            categoriaService.getCategorias(true)
    );

    model.addAttribute(
            "productos",
            productoService.getProductosPorCategoria(idCategoria)
    );

    model.addAttribute(
            "idCategoriaActual",
            idCategoria
    );

    return "index";
    }
    
    @GetMapping("/listado")
    public String listado(Model model) {
    model.addAttribute(
            "productos",
            productoService.getProductos(true)
    );
    return "/consultas/listado";
    }
    
    @PostMapping("/consultaDerivada")
    public String consultaDerivada(
        @RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup,
        Model model) {

    var lista = productoService
            .consultaDerivada(precioInf, precioSup);

    model.addAttribute("productos", lista);

    model.addAttribute("precioInf", precioInf);
    model.addAttribute("precioSup", precioSup);

    return "/consultas/listado";
    }
    
    @PostMapping("/consultaJPQL")
    public String consultaJPQL(
        @RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup,
        Model model) {

    var lista = productoService.consultaJPQL(precioInf, precioSup);

    model.addAttribute("productos", lista);
    model.addAttribute("precioInf", precioInf);
    model.addAttribute("precioSup", precioSup);

    return "/consultas/listado";
    }
    
    @PostMapping("/consultaSQL")
        public String consultaSQL(
        @RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup,
        Model model) {

    var lista = productoService.consultaSQL(precioInf, precioSup);

    model.addAttribute("productos", lista);
    model.addAttribute("precioInf", precioInf);
    model.addAttribute("precioSup", precioSup);

    return "/consultas/listado";
    }
    
}

