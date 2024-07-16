package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;
@Entity
public class Recepta implements Serializable {
    public Recepta(){

    }
    public Recepta(String nom, String descripcio) {
        this.nom = nom;
        this.descripcio = descripcio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nom;

    private String descripcio;

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Categoria> categories;

    public void setUsuari(User usuari) {
        this.usuari = usuari;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    private User usuari;

    @ManyToMany
    private Collection<Ingredient> ingredients;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(Views.Public.class)
    public String getNom() {
        return nom;
    }

    @JsonView(Views.Public.class)
    public String getDescripcio() {
        return descripcio;
    }

    public Collection<Categoria> getCategories() {
        return categories;
    }

    public User getUsuari() {
        return usuari;
    }

}
