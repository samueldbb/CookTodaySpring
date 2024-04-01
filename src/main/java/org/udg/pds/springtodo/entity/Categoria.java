package org.udg.pds.springtodo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Categoria implements Serializable {
    public Categoria(){

    }
    public Categoria(String nom) {
        this.nom = nom;
        this.sub_categories = new ArrayList<>();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nom;

    public Collection<Categoria> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(Collection<Categoria> sub_categories) {
        this.sub_categories = sub_categories;
    }

    @OneToMany
    private Collection<Categoria> sub_categories;
}
