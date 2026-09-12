package com.germanggiorgis.gestortareas.repository;

import com.germanggiorgis.gestortareas.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByUsuarioId(Long usuarioId);
    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);
}
