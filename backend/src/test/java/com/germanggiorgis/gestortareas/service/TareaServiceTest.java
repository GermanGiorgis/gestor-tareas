package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.MoverTareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaRequest;
import com.germanggiorgis.gestortareas.dto.TareaResponse;
import com.germanggiorgis.gestortareas.model.*;
import com.germanggiorgis.gestortareas.repository.CategoriaRepository;
import com.germanggiorgis.gestortareas.repository.TareaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TareaServiceTest {

    @Mock TareaRepository tareaRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock UsuarioContextService usuarioContextService;
    @Mock PaqueteService paqueteService;

    private TareaService tareaService() {
        return new TareaService(tareaRepository, categoriaRepository, usuarioContextService, paqueteService);
    }

    private Usuario usuario() {
        return Usuario.builder().id(1L).email("ana@example.com").password("hash").nombre("Ana").build();
    }

    private Paquete paquete() {
        return Paquete.builder().id(10L).nombre("Mis tareas").creador(usuario()).build();
    }

    @Test
    void crear_usaEstadoYPrioridadPorDefectoCuandoNoSeEnvian() {
        when(paqueteService.verificarMiembro(10L)).thenReturn(paquete());
        when(usuarioContextService.getUsuarioActual()).thenReturn(usuario());
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        var request = new TareaRequest("Lavar los platos", null, null, null, null, null);
        TareaResponse response = tareaService().crear(10L, request);

        assertThat(response.estado()).isEqualTo(EstadoTarea.PENDIENTE);
        assertThat(response.prioridad()).isEqualTo(Prioridad.MEDIA);

        ArgumentCaptor<Tarea> captor = ArgumentCaptor.forClass(Tarea.class);
        verify(tareaRepository).save(captor.capture());
        assertThat(captor.getValue().getPaquete().getId()).isEqualTo(10L);
    }

    @Test
    void listar_filtraPorTextoEnTituloYDescripcion() {
        when(paqueteService.verificarMiembro(10L)).thenReturn(paquete());
        Tarea coincide = Tarea.builder().id(1L).titulo("Comprar pan").paquete(paquete()).creadoPor(usuario()).build();
        Tarea noCoincide = Tarea.builder().id(2L).titulo("Pagar el alquiler").paquete(paquete()).creadoPor(usuario()).build();
        when(tareaRepository.findByPaqueteIdOrderByOrdenAsc(10L)).thenReturn(List.of(coincide, noCoincide));

        List<TareaResponse> resultado = tareaService().listar(10L, null, null, "pan");

        assertThat(resultado).extracting(TareaResponse::titulo).containsExactly("Comprar pan");
    }

    @Test
    void mover_actualizaEstadoYOrden() {
        when(paqueteService.verificarMiembro(10L)).thenReturn(paquete());
        Tarea tarea = Tarea.builder().id(1L).titulo("Comprar pan").estado(EstadoTarea.PENDIENTE).orden(0)
                .paquete(paquete()).creadoPor(usuario()).build();
        when(tareaRepository.findByIdAndPaqueteId(1L, 10L)).thenReturn(Optional.of(tarea));
        when(tareaRepository.save(any(Tarea.class))).thenAnswer(inv -> inv.getArgument(0));

        TareaResponse response = tareaService().mover(10L, 1L, new MoverTareaRequest(EstadoTarea.EN_PROGRESO, 2));

        assertThat(response.estado()).isEqualTo(EstadoTarea.EN_PROGRESO);
        assertThat(response.orden()).isEqualTo(2);
    }

    @Test
    void eliminar_lanzaNotFoundSiLaTareaNoPerteneceAlPaquete() {
        when(paqueteService.verificarMiembro(10L)).thenReturn(paquete());
        when(tareaRepository.findByIdAndPaqueteId(99L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tareaService().eliminar(10L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
