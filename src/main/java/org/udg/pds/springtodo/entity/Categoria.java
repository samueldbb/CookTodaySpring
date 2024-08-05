package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonView;
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
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView(Views.Public.class)
    @NotNull
    private String nom;

    public Long getId() {
        return id;
    }


}
