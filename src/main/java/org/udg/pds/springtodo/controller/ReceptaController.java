package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.CategoriaService;
import org.udg.pds.springtodo.service.ReceptaService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


@RequestMapping(path= "/receptes")
@RestController
public class ReceptaController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(ReceptaService.class);

    @Autowired
    ReceptaService receptaService;

    @GetMapping(path = "/{id}")
    public Recepta getRecepta(HttpSession session,
                                    @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);
        return receptaService.getRecepta(id);
    }

    @GetMapping(path = "/me/{id}")
    public Recepta getReceptaUsuari(HttpSession session,
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

    @PutMapping(path = "/edit/{id}")
    public Recepta updateRecepta(HttpSession session, @PathVariable("id") Long id,
        @Valid @RequestBody ReceptaController.UpdateReceptaData receptaData) {

        Long userId = getLoggedUser(session);

        return receptaService.updateRecepta(userId, id, receptaData.nom, receptaData.descripcio, receptaData.ingredients, receptaData.passos);
    }
    @GetMapping(path = "/conte/{paraula}")
    public Collection<Recepta> getReceptesAmbParaula(HttpSession session,
                                                           @PathVariable("paraula") String paraula) {
        Long userId = getLoggedUser(session);
        if(userId==null) {
            return receptaService.getReceptesBuscador(paraula);
        }
        else{
            return receptaService.getReceptesBuscador(paraula, userId);
        }

    }
    static class R_recepta {

        @NotNull
        public String nom;

        @NotNull
        public String descripcio;

        @NotNull
        public Collection<String> categories = new ArrayList<String>();

        @NotNull
        public String imageUrl;

        @NotNull
        public String ingredients;

        @NotNull
        public String passos;

    }

    static class UpdateReceptaData {
        @NotNull
        public String nom;

        @NotNull
        public String descripcio;

        @NotNull
        public String ingredients;

        @NotNull
        public String passos;
    }


}
