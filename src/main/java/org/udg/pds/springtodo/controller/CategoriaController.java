package org.udg.pds.springtodo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.service.CategoriaService;
import org.udg.pds.springtodo.service.ReceptaService;

import java.util.Collection;

@RequestMapping(path = "/categories")
@RestController
public class CategoriaController extends BaseController{

    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ReceptaService producteService;

    @GetMapping(path="/{id}")
    public Categoria getCategoria(HttpSession session,
                                  @PathVariable("id") Long id) {

        return categoriaService.getCategoria(id);
    }

    @GetMapping
    public Collection<Categoria> getCategories(HttpSession session) {

        return categoriaService.getCategories();
    }

    @PostMapping(consumes = "application/json")
    public String addCategoria(@Valid @RequestBody R_Categoria cat, HttpSession session) {

        categoriaService.addCategoria(cat.nom);
        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path = "/{id}")
    public String deleteCategoria(HttpSession session,
                                  @PathVariable("id") Long id) {

        categoriaService.crud().deleteById(id);
        return BaseController.OK_MESSAGE;
    }

    static class R_Categoria {
        @NotNull
        public String nom;
    }
}
