package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.Paquete;
import com.germanggiorgis.gestortareas.model.RolPaquete;

public record PaqueteResponse(
        Long id,
        String nombre,
        RolPaquete miRol,
        int cantidadMiembros
) {
    public static PaqueteResponse from(Paquete paquete, RolPaquete miRol, int cantidadMiembros) {
        return new PaqueteResponse(paquete.getId(), paquete.getNombre(), miRol, cantidadMiembros);
    }
}
