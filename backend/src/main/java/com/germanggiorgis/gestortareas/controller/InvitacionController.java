package com.germanggiorgis.gestortareas.controller;

import com.germanggiorgis.gestortareas.dto.InvitacionResponse;
import com.germanggiorgis.gestortareas.service.PaqueteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitaciones")
@RequiredArgsConstructor
public class InvitacionController {

    private final PaqueteService paqueteService;

    @GetMapping
    public List<InvitacionResponse> misInvitaciones() {
        return paqueteService.misInvitacionesPendientes();
    }

    @PostMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptar(@PathVariable Long id) {
        paqueteService.aceptarInvitacion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rechazar")
    public ResponseEntity<Void> rechazar(@PathVariable Long id) {
        paqueteService.rechazarInvitacion(id);
        return ResponseEntity.noContent().build();
    }
}
