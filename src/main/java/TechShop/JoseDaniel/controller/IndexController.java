package TechShop.JoseDaniel.controller;

import TechShop.JoseDaniel.service.CategoriaService;
import TechShop.JoseDaniel.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public IndexController(CategoriaService categoriaService,
                           ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {

        model.addAttribute(
                "categorias",
                categoriaService.getCategorias(true)
        );

        model.addAttribute(
                "productos",
                productoService.getProductos(true)
        );

        model.addAttribute(
                "idCategoriaActual",
                null
        );

        return "index";
    }
}