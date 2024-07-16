package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.ReceptaService;

import java.util.Collection;
import java.util.Date;

@RequestMapping(path= "/receptes")
@RestController
public class ReceptaController extends BaseController{
    @Autowired
    ReceptaService receptaService;


    /*
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
*/
    static class R_recepta {

        @NotNull
        public String nom;

        @NotNull
        public String descripcio;

    }
    /*
    @GetMapping(path = "/{id}")
    public Producte getRecepta(HttpSession session,
                                @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);
        return receptaService.getRecepta(userId, id);
    }
    @DeleteMapping(path = "/{id}")
    public String deleteRecepta(HttpSession session,
                             @PathVariable("id") Long receptaId) {
        getLoggedUser(session);
        receptaService.crud().deleteById(receptaId);
        return BaseController.OK_MESSAGE;
    }
*/
}
