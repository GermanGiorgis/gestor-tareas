package com.germanggiorgis.gestortareas.dto;

public record AuthResponse(
        String token,
        String email,
        String nombre
) {
}
