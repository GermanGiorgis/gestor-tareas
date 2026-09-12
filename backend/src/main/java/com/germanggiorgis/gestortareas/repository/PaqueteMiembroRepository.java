package com.germanggiorgis.gestortareas.repository;

import com.germanggiorgis.gestortareas.model.PaqueteMiembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaqueteMiembroRepository extends JpaRepository<PaqueteMiembro, Long> {
    List<PaqueteMiembro> findByUsuarioIdOrderByJoinedAtAsc(Long usuarioId);
    List<PaqueteMiembro> findByPaqueteIdOrderByJoinedAtAsc(Long paqueteId);
    Optional<PaqueteMiembro> findByPaqueteIdAndUsuarioId(Long paqueteId, Long usuarioId);
    boolean existsByPaqueteIdAndUsuarioId(Long paqueteId, Long usuarioId);
    void deleteByPaqueteIdAndUsuarioId(Long paqueteId, Long usuarioId);
}
