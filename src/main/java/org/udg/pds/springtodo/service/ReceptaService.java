package org.udg.pds.springtodo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.configuration.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Recepta;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.CategoriaRepository;
import org.udg.pds.springtodo.repository.ReceptaRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class ReceptaService{

    @Autowired
    ReceptaRepository receptaRepository;

    @Lazy
    @Autowired
    UserService userService;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    CategoriaRepository categoriaRepository;

    public ReceptaRepository crud() {
        return receptaRepository;
    }

    @Transactional
    public IdObject addRecepta(String nom, Long userId, String descripcio, Collection<String> cats, String imageUrl) {
        try {
            User user = userService.getUser(userId);

            Recepta recepta = new Recepta(nom, descripcio, imageUrl);

            recepta.setUsuari(user);

            user.addRecepta(recepta);

            Collection<Categoria> categoriesAux = new ArrayList<>();
            for (String categoriaNom : cats) {
                Categoria existingCategory = categoriaService.getCategoriaByNom(categoriaNom);
                categoriesAux.add(existingCategory);
            }
            recepta.setCategories(categoriesAux);

            receptaRepository.save(recepta);
            userService.crud().save(user);
            return new IdObject(recepta.getId());
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    public Recepta getRecepta(Long userId, Long id) {
        Optional<Recepta> recepta = receptaRepository.findById(id);
        if (recepta.isEmpty()) throw new ServiceException("Aquesta recepta no existeix");
        if (recepta.get().getUsuari().getId() != userId)
            throw new ServiceException("L'usuari no te aquesta recepta");
        return recepta.get();
    }
    public Collection<Recepta> getReceptes(Long id) {
        return userService.getUser(id).getReceptes();
    }


    public Collection<Recepta> getReceptesCategoria(Long idCat,Long idUser){

        Collection<Recepta> totesReceptes = receptaRepository.findAll();

        Collection<Recepta> llistaFiltradaperCategories = new ArrayList<>();
        if(idCat==0) llistaFiltradaperCategories = totesReceptes;
        else {
            llistaFiltradaperCategories.clear();
            for(Recepta r : totesReceptes) {
                Collection<Categoria> llistaCat = r.getCategories();
                for(Categoria c : llistaCat) {
                    if (c.getId() == idCat) {
                        llistaFiltradaperCategories.add(r);
                    }
                }
            }
        }
        Collection<Recepta> altresReceptes = new ArrayList<>();
        for (Recepta i : llistaFiltradaperCategories)
        {
            if (i.getUsuari() == null || i.getUsuari().getId() != idUser) altresReceptes.add(i);
        }


        return altresReceptes;
    }

    public Collection<Recepta> getAllReceptesCategoria(Long idCat){

        Collection<Recepta> totesReceptes = receptaRepository.findAll();

        Collection<Recepta> llistaFiltradaperCategories = new ArrayList<>();
        if(idCat==0) llistaFiltradaperCategories = totesReceptes;
        else {
            llistaFiltradaperCategories.clear();
            for(Recepta r : totesReceptes) {
                Collection<Categoria> llistaCat = r.getCategories();
                for(Categoria c : llistaCat) {
                    if (c.getId() == idCat) {
                        llistaFiltradaperCategories.add(r);
                    }
                }
            }
        }

        return llistaFiltradaperCategories;
    }

    public Collection<Recepta> getReceptesBuscador(String paraula) {
        Collection<Recepta> totesReceptes = receptaRepository.findAll();

        Collection<Recepta> llistaFiltradaperParaula = new ArrayList<>();
        if(paraula.isEmpty()) llistaFiltradaperParaula = totesReceptes;
        else {
            String paraulaMinuscula = paraula.toLowerCase();
            for(Recepta r : totesReceptes){
                if(r.getNom().toLowerCase().contains(paraulaMinuscula)){
                    llistaFiltradaperParaula.add(r);
                }
            }
        }

        return llistaFiltradaperParaula;
    }

    public Collection<Recepta> getReceptesBuscador(String paraula, Long idUser) {
        Collection<Recepta> totesReceptes = receptaRepository.findAll();

        Collection<Recepta> llistaFiltradaperParaula = new ArrayList<>();
        if (paraula.isEmpty()) {
            llistaFiltradaperParaula = totesReceptes;
        } else {
            String paraulaMinuscula = paraula.toLowerCase();
            for (Recepta r : totesReceptes) {
                if (r.getNom().toLowerCase().contains(paraulaMinuscula)) {
                    llistaFiltradaperParaula.add(r);
                }
            }
        }

        Collection<Recepta> altresReceptes = new ArrayList<>();
        for (Recepta i : llistaFiltradaperParaula) {
            if (i.getUsuari() == null || !i.getUsuari().getId().equals(idUser)) {
                altresReceptes.add(i);
            }
        }

        return altresReceptes;
    }

    public Recepta getRecepta(Long id) {
        Optional<Recepta> recepta = receptaRepository.findById(id);
        if (recepta.isEmpty()) throw new ServiceException("Aquesta recepta no existeix");
        else
            return recepta.get();
    }

}
