package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.EstadoTarea;
import com.germanggiorgis.gestortareas.model.Prioridad;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TareaRequest(
        @NotBlank String titulo,
        String descripcion,
        EstadoTarea estado,
        Prioridad prioridad,
        LocalDate fechaLimite,
        Long categoriaId
) {
}
