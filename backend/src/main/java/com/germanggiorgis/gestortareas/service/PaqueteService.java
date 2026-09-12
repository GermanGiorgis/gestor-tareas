package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.*;
import com.germanggiorgis.gestortareas.model.*;
import com.germanggiorgis.gestortareas.repository.InvitacionPaqueteRepository;
import com.germanggiorgis.gestortareas.repository.PaqueteMiembroRepository;
import com.germanggiorgis.gestortareas.repository.PaqueteRepository;
import com.germanggiorgis.gestortareas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final PaqueteMiembroRepository miembroRepository;
    private final InvitacionPaqueteRepository invitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioContextService usuarioContextService;

    public List<PaqueteResponse> misPaquetes() {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        return miembroRepository.findByUsuarioIdOrderByJoinedAtAsc(usuario.getId()).stream()
                .map(m -> PaqueteResponse.from(
                        m.getPaquete(),
                        m.getRol(),
                        miembroRepository.findByPaqueteIdOrderByJoinedAtAsc(m.getPaquete().getId()).size()
                ))
                .toList();
    }

    public PaqueteResponse crear(PaqueteRequest request) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        Paquete paquete = crearPaqueteConPropietario(request.nombre(), usuario);
        return PaqueteResponse.from(paquete, RolPaquete.PROPIETARIO, 1);
    }

    /** Crea un paquete y da de alta a su creador como propietario. Usado también al registrar un usuario nuevo. */
    public Paquete crearPaqueteConPropietario(String nombre, Usuario propietario) {
        Paquete paquete = paqueteRepository.save(Paquete.builder()
                .nombre(nombre)
                .creador(propietario)
                .build());

        miembroRepository.save(PaqueteMiembro.builder()
                .paquete(paquete)
                .usuario(propietario)
                .rol(RolPaquete.PROPIETARIO)
                .build());

        return paquete;
    }

    public List<MiembroResponse> listarMiembros(Long paqueteId) {
        verificarMiembro(paqueteId);
        return miembroRepository.findByPaqueteIdOrderByJoinedAtAsc(paqueteId).stream()
                .map(MiembroResponse::from)
                .toList();
    }

    public InvitacionResponse invitar(Long paqueteId, InvitacionRequest request) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        Paquete paquete = obtenerComoPropietario(paqueteId, usuario);

        String email = request.email().trim();

        if (email.equalsIgnoreCase(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podés invitarte a vos mismo");
        }

        boolean yaEsMiembro = usuarioRepository.findByEmail(email)
                .map(u -> miembroRepository.existsByPaqueteIdAndUsuarioId(paqueteId, u.getId()))
                .orElse(false);
        if (yaEsMiembro) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese usuario ya es miembro del paquete");
        }

        if (invitacionRepository.existsByPaqueteIdAndEmailInvitadoIgnoreCaseAndEstado(paqueteId, email, EstadoInvitacion.PENDIENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya hay una invitación pendiente para ese email");
        }

        InvitacionPaquete invitacion = invitacionRepository.save(InvitacionPaquete.builder()
                .paquete(paquete)
                .emailInvitado(email)
                .invitadoPor(usuario)
                .build());

        return InvitacionResponse.from(invitacion);
    }

    public List<InvitacionResponse> misInvitacionesPendientes() {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        return invitacionRepository.findByEmailInvitadoIgnoreCaseAndEstado(usuario.getEmail(), EstadoInvitacion.PENDIENTE).stream()
                .map(InvitacionResponse::from)
                .toList();
    }

    public void aceptarInvitacion(Long invitacionId) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        InvitacionPaquete invitacion = obtenerInvitacionPendiente(invitacionId, usuario);

        if (!miembroRepository.existsByPaqueteIdAndUsuarioId(invitacion.getPaquete().getId(), usuario.getId())) {
            miembroRepository.save(PaqueteMiembro.builder()
                    .paquete(invitacion.getPaquete())
                    .usuario(usuario)
                    .rol(RolPaquete.MIEMBRO)
                    .build());
        }

        invitacion.setEstado(EstadoInvitacion.ACEPTADA);
        invitacionRepository.save(invitacion);
    }

    public void rechazarInvitacion(Long invitacionId) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        InvitacionPaquete invitacion = obtenerInvitacionPendiente(invitacionId, usuario);
        invitacion.setEstado(EstadoInvitacion.RECHAZADA);
        invitacionRepository.save(invitacion);
    }

    public void eliminarMiembro(Long paqueteId, Long usuarioId) {
        Usuario actual = usuarioContextService.getUsuarioActual();
        Paquete paquete = obtenerComoPropietario(paqueteId, actual);
        if (paquete.getCreador().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El propietario no puede salir del paquete");
        }
        miembroRepository.deleteByPaqueteIdAndUsuarioId(paqueteId, usuarioId);
    }

    public void eliminarPaquete(Long paqueteId) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        obtenerComoPropietario(paqueteId, usuario);
        paqueteRepository.deleteById(paqueteId);
    }

    /** Confirma que el usuario autenticado es miembro del paquete; lanza 404 si no. Usado por TareaService. */
    public Paquete verificarMiembro(Long paqueteId) {
        Usuario usuario = usuarioContextService.getUsuarioActual();
        miembroRepository.findByPaqueteIdAndUsuarioId(paqueteId, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));
        return paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));
    }

    private Paquete obtenerComoPropietario(Long paqueteId, Usuario usuario) {
        PaqueteMiembro miembro = miembroRepository.findByPaqueteIdAndUsuarioId(paqueteId, usuario.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paquete no encontrado"));
        if (miembro.getRol() != RolPaquete.PROPIETARIO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el propietario puede hacer esto");
        }
        return miembro.getPaquete();
    }

    private InvitacionPaquete obtenerInvitacionPendiente(Long invitacionId, Usuario usuario) {
        InvitacionPaquete invitacion = invitacionRepository.findById(invitacionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitación no encontrada"));
        if (!invitacion.getEmailInvitado().equalsIgnoreCase(usuario.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta invitación no es para vos");
        }
        if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta invitación ya fue respondida");
        }
        return invitacion;
    }
}
