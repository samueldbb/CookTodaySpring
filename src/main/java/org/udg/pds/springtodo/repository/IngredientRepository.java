package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udg.pds.springtodo.entity.Ingredient;
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
