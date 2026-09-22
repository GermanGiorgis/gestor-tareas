package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.InvitacionRequest;
import com.germanggiorgis.gestortareas.model.*;
import com.germanggiorgis.gestortareas.repository.InvitacionPaqueteRepository;
import com.germanggiorgis.gestortareas.repository.PaqueteMiembroRepository;
import com.germanggiorgis.gestortareas.repository.PaqueteRepository;
import com.germanggiorgis.gestortareas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceTest {

    @Mock PaqueteRepository paqueteRepository;
    @Mock PaqueteMiembroRepository miembroRepository;
    @Mock InvitacionPaqueteRepository invitacionRepository;
    @Mock UsuarioRepository usuarioRepository;
    @Mock UsuarioContextService usuarioContextService;

    private PaqueteService paqueteService() {
        return new PaqueteService(paqueteRepository, miembroRepository, invitacionRepository, usuarioRepository,
                usuarioContextService);
    }

    private Usuario usuario(Long id, String email) {
        return Usuario.builder().id(id).email(email).password("hash").nombre("Usuario " + id).build();
    }

    private PaqueteMiembro miembro(Paquete paquete, Usuario usuario, RolPaquete rol) {
        return PaqueteMiembro.builder().paquete(paquete).usuario(usuario).rol(rol).build();
    }

    @Test
    void invitar_rechazaInvitarseASiMismo() {
        Usuario ana = usuario(1L, "ana@example.com");
        Paquete paquete = Paquete.builder().id(10L).nombre("Mis tareas").creador(ana).build();
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(miembro(paquete, ana, RolPaquete.PROPIETARIO)));

        assertThatThrownBy(() -> paqueteService().invitar(10L, new InvitacionRequest("ana@example.com")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("No podés invitarte a vos mismo");
    }

    @Test
    void invitar_rechazaSiElInvitadoYaEsMiembro() {
        Usuario ana = usuario(1L, "ana@example.com");
        Usuario bruno = usuario(2L, "bruno@example.com");
        Paquete paquete = Paquete.builder().id(10L).nombre("Mis tareas").creador(ana).build();
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(miembro(paquete, ana, RolPaquete.PROPIETARIO)));
        when(usuarioRepository.findByEmail("bruno@example.com")).thenReturn(Optional.of(bruno));
        when(miembroRepository.existsByPaqueteIdAndUsuarioId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> paqueteService().invitar(10L, new InvitacionRequest("bruno@example.com")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void invitar_rechazaSiYaHayUnaInvitacionPendiente() {
        Usuario ana = usuario(1L, "ana@example.com");
        Paquete paquete = Paquete.builder().id(10L).nombre("Mis tareas").creador(ana).build();
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(miembro(paquete, ana, RolPaquete.PROPIETARIO)));
        when(usuarioRepository.findByEmail("bruno@example.com")).thenReturn(Optional.empty());
        when(invitacionRepository.existsByPaqueteIdAndEmailInvitadoIgnoreCaseAndEstado(
                10L, "bruno@example.com", EstadoInvitacion.PENDIENTE)).thenReturn(true);

        assertThatThrownBy(() -> paqueteService().invitar(10L, new InvitacionRequest("bruno@example.com")))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void invitar_creaLaInvitacionCuandoTodoEsValido() {
        Usuario ana = usuario(1L, "ana@example.com");
        Paquete paquete = Paquete.builder().id(10L).nombre("Mis tareas").creador(ana).build();
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(miembro(paquete, ana, RolPaquete.PROPIETARIO)));
        when(usuarioRepository.findByEmail("bruno@example.com")).thenReturn(Optional.empty());
        when(invitacionRepository.existsByPaqueteIdAndEmailInvitadoIgnoreCaseAndEstado(
                10L, "bruno@example.com", EstadoInvitacion.PENDIENTE)).thenReturn(false);
        when(invitacionRepository.save(any(InvitacionPaquete.class))).thenAnswer(inv -> inv.getArgument(0));

        paqueteService().invitar(10L, new InvitacionRequest("bruno@example.com"));

        verify(invitacionRepository).save(any(InvitacionPaquete.class));
    }

    @Test
    void verificarMiembro_lanzaNotFoundSiElUsuarioNoPerteneceAlPaquete() {
        Usuario ana = usuario(1L, "ana@example.com");
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paqueteService().verificarMiembro(10L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void eliminarMiembro_noDejaQueElPropietarioSeEliminEASiMismo() {
        Usuario ana = usuario(1L, "ana@example.com");
        Paquete paquete = Paquete.builder().id(10L).nombre("Mis tareas").creador(ana).build();
        when(usuarioContextService.getUsuarioActual()).thenReturn(ana);
        when(miembroRepository.findByPaqueteIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(miembro(paquete, ana, RolPaquete.PROPIETARIO)));

        assertThatThrownBy(() -> paqueteService().eliminarMiembro(10L, 1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("El propietario no puede salir del paquete");

        verify(miembroRepository, never()).deleteByPaqueteIdAndUsuarioId(any(), any());
    }
}
