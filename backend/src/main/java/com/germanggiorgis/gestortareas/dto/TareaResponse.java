package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.EstadoTarea;
import com.germanggiorgis.gestortareas.model.Prioridad;
import com.germanggiorgis.gestortareas.model.Tarea;

import java.time.LocalDate;

public record TareaResponse(
        Long id,
        String titulo,
        String descripcion,
        EstadoTarea estado,
        Prioridad prioridad,
        LocalDate fechaLimite,
        Integer orden,
        CategoriaResponse categoria,
        String creadoPorNombre
) {
    public static TareaResponse from(Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getEstado(),
                tarea.getPrioridad(),
                tarea.getFechaLimite(),
                tarea.getOrden(),
                tarea.getCategoria() != null ? CategoriaResponse.from(tarea.getCategoria()) : null,
                tarea.getCreadoPor().getNombre()
        );
    }
}
