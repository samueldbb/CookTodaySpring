package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.ReceptaService;

import java.util.Collection;
import java.util.Date;

public class ReceptaController extends BaseController{
    @Autowired
    ReceptaService receptaService;

    @PostMapping
    public IdObject addRecepta(HttpSession session, @Valid @RequestBody ReceptaController.R_recepta recepta) {
        Long userId = getLoggedUser(session);

        return receptaService.addRecepta(recepta.nom, userId, recepta.descripcio);
    }

    @GetMapping
    @JsonView(Views.Private.class)
    public Collection<Recepta> listAllReceptes(HttpSession session,
                                                @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);
        return receptaService.getReceptes(userId);
    }

    static class R_recepta {

        @NotNull
        public String nom;

        @NotNull
        public String descripcio;

    }
    /*
    @GetMapping(path = "/{id}")
    public Producte getProducte(HttpSession session,
                                @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);
        return producteService.getProducte(userId, id);
    }
    @DeleteMapping(path = "/{id}")
    public String deleteProducte(HttpSession session,
                             @PathVariable("id") Long producteId) {
        getLoggedUser(session);
        producteService.crud().deleteById(producteId);
        return BaseController.OK_MESSAGE;
    }
*/
}
