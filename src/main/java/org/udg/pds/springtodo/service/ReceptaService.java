package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
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
    protected TagService tagService;
    @Autowired
    ReceptaRepository receptaRepository;
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
        Optional<Recepta> p = receptaRepository.findById(id);             //error?
        if (p.isEmpty()) throw new ServiceException("Recipe does not exists");
        if (p.get().getUsuari().getId() != userId)
            throw new ServiceException("User does not own this recipe");
        return p.get();
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

}
