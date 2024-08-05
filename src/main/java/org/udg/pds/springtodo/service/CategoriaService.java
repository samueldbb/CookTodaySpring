package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.configuration.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Categoria;
import org.udg.pds.springtodo.repository.CategoriaRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CategoriaService {
    @Autowired
    CategoriaRepository categoriaRepository;

    public CategoriaRepository crud() {
        return categoriaRepository;
    }

    public Collection<Categoria> getCategories(){
        Collection<Categoria> categories= StreamSupport
                    .stream(categoriaRepository.findAll().spliterator(), false).collect(Collectors.toList());

        return categories;
    }
    @Transactional
    public Categoria addCategoria(String name) {
        try {
            if (name == null) {
                throw new ServiceException("La categoria no pot ser nula");
            }

            Categoria cat = new Categoria(name);

            categoriaRepository.save(cat);
            return cat;
        } catch (Exception ex) {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new ServiceException(ex.getMessage());
        }
    }

    public Categoria getCategoria(Long id){
        Optional<Categoria> cat = categoriaRepository.findById(id);
        if(!cat.isPresent()) throw new ServiceException("La categoria no existeix");
        else return cat.get();
    }
}
