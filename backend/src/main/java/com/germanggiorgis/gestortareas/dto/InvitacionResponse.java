package com.germanggiorgis.gestortareas.dto;

import com.germanggiorgis.gestortareas.model.EstadoInvitacion;
import com.germanggiorgis.gestortareas.model.InvitacionPaquete;

import java.time.Instant;

public record InvitacionResponse(
        Long id,
        Long paqueteId,
        String paqueteNombre,
        String emailInvitado,
        String invitadoPorNombre,
        EstadoInvitacion estado,
        Instant createdAt
) {
    public static InvitacionResponse from(InvitacionPaquete invitacion) {
        return new InvitacionResponse(
                invitacion.getId(),
                invitacion.getPaquete().getId(),
                invitacion.getPaquete().getNombre(),
                invitacion.getEmailInvitado(),
                invitacion.getInvitadoPor().getNombre(),
                invitacion.getEstado(),
                invitacion.getCreatedAt()
        );
    }
}
