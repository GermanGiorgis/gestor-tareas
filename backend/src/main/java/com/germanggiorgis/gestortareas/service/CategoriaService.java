package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.CategoriaRequest;
import com.germanggiorgis.gestortareas.dto.CategoriaResponse;
import com.germanggiorgis.gestortareas.model.Categoria;
import com.germanggiorgis.gestortareas.model.Usuario;
import com.germanggiorgis.gestortareas.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioContextService usuarioContextService;

    public List<CategoriaResponse> listar() {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        return categoriaRepository.findByUsuarioId(usuario.getId()).stream()
                .map(CategoriaResponse::from)
                .toList();
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        Categoria categoria = Categoria.builder()
                .nombre(request.nombre())
                .color(request.color())
                .usuario(usuario)
                .build();
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    public void eliminar(Long id) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        if (!categoriaRepository.existsByIdAndUsuarioId(id, usuario.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}
