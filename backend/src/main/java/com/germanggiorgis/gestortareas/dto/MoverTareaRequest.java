package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.EstadoTarea;
import jakarta.validation.constraints.NotNull;

public record MoverTareaRequest(
        @NotNull EstadoTarea estado,
        @NotNull Integer orden
) {
}
