package com.germanggiorgis.gestortareas.repository;

import com.germanggiorgis.gestortareas.model.EstadoInvitacion;
import com.germanggiorgis.gestortareas.model.InvitacionPaquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitacionPaqueteRepository extends JpaRepository<InvitacionPaquete, Long> {
    List<InvitacionPaquete> findByEmailInvitadoIgnoreCaseAndEstado(String email, EstadoInvitacion estado);
    List<InvitacionPaquete> findByPaqueteIdOrderByCreatedAtDesc(Long paqueteId);
    boolean existsByPaqueteIdAndEmailInvitadoIgnoreCaseAndEstado(Long paqueteId, String email, EstadoInvitacion estado);
}
