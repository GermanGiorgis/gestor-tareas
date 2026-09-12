package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.Categoria;

public record CategoriaResponse(
        Long id,
        String nombre,
        String color
) {
    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getColor());
    }
}
