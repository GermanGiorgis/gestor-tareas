package com.germanggiorgis.gestortareas.controller;

import com.germanggiorgis.gestortareas.dto.MoverTareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaResponse;
import com.germanggiorgis.gestortareas.model.EstadoTarea;
import com.germanggiorgis.gestortareas.service.TareaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes/{paqueteId}/tareas")
@RequiredArgsConstructor
public class TareaController {

    private final TareaService tareaService;

    @GetMapping
    public List<TareaResponse> listar(
            @PathVariable Long paqueteId,
            @RequestParam(required = false) EstadoTarea estado,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String texto
    ) {
        return tareaService.listar(paqueteId, estado, categoriaId, texto);
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@PathVariable Long paqueteId, @Valid @RequestBody TareaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tareaService.crear(paqueteId, request));
    }

    @PutMapping("/{id}")
    public TareaResponse actualizar(@PathVariable Long paqueteId, @PathVariable Long id, @Valid @RequestBody TareaRequest request) {
        return tareaService.actualizar(paqueteId, id, request);
    }

    @PatchMapping("/{id}/mover")
    public TareaResponse mover(@PathVariable Long paqueteId, @PathVariable Long id, @Valid @RequestBody MoverTareaRequest request) {
        return tareaService.mover(paqueteId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long paqueteId, @PathVariable Long id) {
        tareaService.eliminar(paqueteId, id);
        return ResponseEntity.noContent().build();
    }
}
