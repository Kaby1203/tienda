package com.tienda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String e403(Model model) {
        return "/acceso_denegado";
    }

    @GetMapping("/error")
    public String errorG(Model model) {
        return "/acceso_denegado";
    }

    @PostMapping("/error")
    public String errorP(Model model) {
        return "/acceso_denegado";
    }
}