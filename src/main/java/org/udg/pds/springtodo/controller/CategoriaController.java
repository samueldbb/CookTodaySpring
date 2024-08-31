package org.udg.pds.springtodo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.service.CategoriaService;
import org.udg.pds.springtodo.service.ReceptaService;

import java.util.Collection;

@RequestMapping(path = "/categories")
@RestController
public class CategoriaController extends BaseController{

    @Autowired
    CategoriaService categoriaService;
    @Autowired
    ReceptaService receptaService;

    @GetMapping(path="/{id}")
    public Categoria getCategoria(HttpSession session,
                                  @PathVariable("id") Long id) {

        return categoriaService.getCategoria(id);
    }

    @PostMapping(consumes = "application/json")
    public String addCategoria(@Valid @RequestBody R_Categoria cat, HttpSession session) {

        categoriaService.addCategoria(cat.nom);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping
    public Collection<Categoria> getCategories(HttpSession session) {

        return categoriaService.getCategories();
    }


    @DeleteMapping(path = "/{id}")
    public String deleteCategoria(HttpSession session,
                                  @PathVariable("id") Long id) {

        categoriaService.crud().deleteById(id);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path = "/{id}/receptes")
    public Collection<Recepta> getCategoriaReceptes(HttpSession session,
                                                    @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);
        if(userId==null)
            return receptaService.getAllReceptesCategoria(id);

        else
            return receptaService.getReceptesCategoria(id, userId);
    }

    static class R_Categoria {
        @NotNull
        public String nom;
    }
}
