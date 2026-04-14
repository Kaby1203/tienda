package com.tienda.controller;

import com.tienda.service.CategoriaService;
import com.tienda.service.ProductoService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {
    
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    
    public ConsultaController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }
    
    @GetMapping("/{idCategoria}")
    public String listadoPorCategoria(@PathVariable("idCategoria") Integer idCategoria, Model model) {
        model.addAttribute("idCategoriaActual", idCategoria);
        var categoriaOptional = categoriaService.getCategoria(idCategoria);
        if (categoriaOptional.isEmpty()) {
            model.addAttribute("productos", java.util.Collections.emptyList());
        } else {
            var categoria = categoriaOptional.get();
            var productos = categoria.getProductos();
            model.addAttribute("productos", productos);
        }
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "/index";
    }
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        return "/consultas/listado";
    }
    

    @PostMapping("/derivadas")  
    public String consultaDerivada(@RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup, Model model) {
        var productos = productoService.consultaDerivada(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    

    @PostMapping("/jpql")  
    public String consultaJPQL(@RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup, Model model) {
        var productos = productoService.consultaJPQL(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    

    @PostMapping("/sql") 
    public String consultaSQL(@RequestParam BigDecimal precioInf,
        @RequestParam BigDecimal precioSup, Model model) {
        var productos = productoService.consultaSQL(precioInf, precioSup);
        model.addAttribute("productos", productos);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        return "/consultas/listado";
    }
    
@PostMapping("/consultaNombre")
public String consultaPorNombre(@RequestParam String nombre, Model model) {
    var productos = productoService.consultaPorNombreJPQL(nombre);
    model.addAttribute("productos", productos);
    model.addAttribute("nombre", nombre);
    return "/consultas/listado";
}
}