package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Recepta.class)
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

    public void setCategories(Collection<Categoria> cats){
        this.categories = cats;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    private User usuari;

    @ManyToMany
    private Collection<Ingredient> ingredients;

    @JsonView(Views.Complete.class)
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

    @JsonView(Views.Public.class)
    public Collection<Categoria> getCategories() {
        return categories;
    }

    public User getUsuari() {
        return usuari;
    }

}
