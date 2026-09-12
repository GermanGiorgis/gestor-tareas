package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.MoverTareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaResponse;
import com.germanggiorgis.gestortareas.model.Categoria;
import com.germanggiorgis.gestortareas.model.EstadoTarea;
import com.germanggiorgis.gestortareas.model.Paquete;
import com.germanggiorgis.gestortareas.model.Prioridad;
import com.germanggiorgis.gestortareas.model.Tarea;
import com.germanggiorgis.gestortareas.model.Usuario;
import com.germanggiorgis.gestortareas.repository.CategoriaRepository;
import com.germanggiorgis.gestortareas.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioContextService usuarioContextService;
    private final PaqueteService paqueteService;

    public List<TareaResponse> listar(Long paqueteId, EstadoTarea estado, Long categoriaId, String texto) {
        paqueteService.verificarMiembro(paqueteId);

        List<Tarea> tareas = estado != null
                ? tareaRepository.findByPaqueteIdAndEstadoOrderByOrdenAsc(paqueteId, estado)
                : tareaRepository.findByPaqueteIdOrderByOrdenAsc(paqueteId);

        return tareas.stream()
                .filter(t -> categoriaId == null || (t.getCategoria() != null && t.getCategoria().getId().equals(categoriaId)))
                .filter(t -> texto == null || texto.isBlank()
                        || t.getTitulo().toLowerCase().contains(texto.toLowerCase())
                        || (t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(texto.toLowerCase())))
                .map(TareaResponse::from)
                .toList();
    }

    public TareaResponse crear(Long paqueteId, TareaRequest request) {
        Paquete paquete = paqueteService.verificarMiembro(paqueteId);
        Usuario usuario = usuarioContextService.getUsuarioActual();
        Categoria categoria = resolverCategoria(request.categoriaId(), usuario);

        Tarea tarea = Tarea.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .estado(request.estado() != null ? request.estado() : EstadoTarea.PENDIENTE)
                .prioridad(request.prioridad() != null ? request.prioridad() : Prioridad.MEDIA)
                .fechaLimite(request.fechaLimite())
                .categoria(categoria)
                .paquete(paquete)
                .creadoPor(usuario)
                .build();

        return TareaResponse.from(tareaRepository.save(tarea));
    }

    public TareaResponse actualizar(Long paqueteId, Long id, TareaRequest request) {
        paqueteService.verificarMiembro(paqueteId);
        Usuario usuario = usuarioContextService.getUsuarioActual();
        Tarea tarea = obtenerDelPaquete(paqueteId, id);
        Categoria categoria = resolverCategoria(request.categoriaId(), usuario);

        tarea.setTitulo(request.titulo());
        tarea.setDescripcion(request.descripcion());
        if (request.estado() != null) tarea.setEstado(request.estado());
        if (request.prioridad() != null) tarea.setPrioridad(request.prioridad());
        tarea.setFechaLimite(request.fechaLimite());
        tarea.setCategoria(categoria);

        return TareaResponse.from(tareaRepository.save(tarea));
    }

    public TareaResponse mover(Long paqueteId, Long id, MoverTareaRequest request) {
        paqueteService.verificarMiembro(paqueteId);
        Tarea tarea = obtenerDelPaquete(paqueteId, id);

        tarea.setEstado(request.estado());
        tarea.setOrden(request.orden());

        return TareaResponse.from(tareaRepository.save(tarea));
    }

    public void eliminar(Long paqueteId, Long id) {
        paqueteService.verificarMiembro(paqueteId);
        Tarea tarea = obtenerDelPaquete(paqueteId, id);
        tareaRepository.delete(tarea);
    }

    private Tarea obtenerDelPaquete(Long paqueteId, Long id) {
        return tareaRepository.findByIdAndPaqueteId(id, paqueteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
    }

    private Categoria resolverCategoria(Long categoriaId, Usuario usuario) {
        if (categoriaId == null) return null;
        return categoriaRepository.findById(categoriaId)
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }
}
