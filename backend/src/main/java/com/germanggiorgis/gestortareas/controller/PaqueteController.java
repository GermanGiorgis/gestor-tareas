package com.germanggiorgis.gestortareas.controller;

import com.germanggiorgis.gestortareas.dto.*;
import com.germanggiorgis.gestortareas.service.PaqueteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
public class PaqueteController {

    private final PaqueteService paqueteService;

    @GetMapping
    public List<PaqueteResponse> misPaquetes() {
        return paqueteService.misPaquetes();
    }

    @PostMapping
    public ResponseEntity<PaqueteResponse> crear(@Valid @RequestBody PaqueteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paqueteService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        paqueteService.eliminarPaquete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/miembros")
    public List<MiembroResponse> miembros(@PathVariable Long id) {
        return paqueteService.listarMiembros(id);
    }

    @DeleteMapping("/{id}/miembros/{usuarioId}")
    public ResponseEntity<Void> eliminarMiembro(@PathVariable Long id, @PathVariable Long usuarioId) {
        paqueteService.eliminarMiembro(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/invitaciones")
    public ResponseEntity<InvitacionResponse> invitar(@PathVariable Long id, @Valid @RequestBody InvitacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paqueteService.invitar(id, request));
    }
}
