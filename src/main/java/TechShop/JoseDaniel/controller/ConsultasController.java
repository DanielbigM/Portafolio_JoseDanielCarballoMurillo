package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.service.CategoriaService;
import TechShop.JoseDaniel.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{idCategoria}")
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
}