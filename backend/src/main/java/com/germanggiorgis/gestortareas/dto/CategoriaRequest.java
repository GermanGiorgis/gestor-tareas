package com.germanggiorgis.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank String nombre,
        @NotBlank String color
) {
}
