package com.germanggiorgis.gestortareas.repository;

import com.germanggiorgis.gestortareas.model.EstadoTarea;
import com.germanggiorgis.gestortareas.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findByPaqueteIdOrderByOrdenAsc(Long paqueteId);
    List<Tarea> findByPaqueteIdAndEstadoOrderByOrdenAsc(Long paqueteId, EstadoTarea estado);
    Optional<Tarea> findByIdAndPaqueteId(Long id, Long paqueteId);
}
