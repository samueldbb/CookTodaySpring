package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udg.pds.springtodo.entity.Recepta;

public interface IngredientRepository extends JpaRepository<Recepta, Long> {
}
