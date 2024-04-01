package org.udg.pds.springtodo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Entity
public class Ingredient implements Serializable {
    public Ingredient(){

    }
    public Ingredient(String nom, String descripcio) {
        this.nom = nom;
        this.descripcio = descripcio;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nom;

    private String descripcio;
}
