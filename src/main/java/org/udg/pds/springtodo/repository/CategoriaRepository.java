package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.udg.pds.springtodo.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
