package com.germanggiorgis.gestortareas.dto;

import jakarta.validation.constraints.NotBlank;

public record PaqueteRequest(
        @NotBlank String nombre
) {
}
