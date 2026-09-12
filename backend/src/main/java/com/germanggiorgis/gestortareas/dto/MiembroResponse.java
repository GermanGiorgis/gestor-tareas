package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.PaqueteMiembro;
import com.germanggiorgis.gestortareas.model.RolPaquete;

public record MiembroResponse(
        Long usuarioId,
        String nombre,
        String email,
        RolPaquete rol
) {
    public static MiembroResponse from(PaqueteMiembro miembro) {
        return new MiembroResponse(
                miembro.getUsuario().getId(),
                miembro.getUsuario().getNombre(),
                miembro.getUsuario().getEmail(),
                miembro.getRol()
        );
    }
}
