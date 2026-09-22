package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.AuthResponse;
import com.germanggiorgis.gestortareas.dto.LoginRequest;
import com.germanggiorgis.gestortareas.dto.RegistroRequest;
import com.germanggiorgis.gestortareas.model.Usuario;
import com.germanggiorgis.gestortareas.repository.CategoriaRepository;
import com.germanggiorgis.gestortareas.repository.UsuarioRepository;
import com.germanggiorgis.gestortareas.security.CustomUserDetailsService;
import com.germanggiorgis.gestortareas.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock CategoriaRepository categoriaRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @Mock AuthenticationManager authenticationManager;
    @Mock CustomUserDetailsService userDetailsService;
    @Mock PaqueteService paqueteService;

    private AuthService authService() {
        return new AuthService(usuarioRepository, categoriaRepository, passwordEncoder, jwtService,
                authenticationManager, userDetailsService, paqueteService);
    }

    @Test
    void registrar_rechazaUnEmailQueYaExiste() {
        when(usuarioRepository.existsByEmail("ana@example.com")).thenReturn(true);
        var request = new RegistroRequest("ana@example.com", "contraseña123", "Ana");

        assertThatThrownBy(() -> authService().registrar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe una cuenta con ese email");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_creaSieteCategoriasPorDefectoYUnPaqueteInicial() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("contraseña123")).thenReturn("hash-seguro");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        UserDetails userDetails = User.builder().username("ana@example.com").password("hash-seguro")
                .authorities(java.util.List.of()).build();
        when(userDetailsService.loadUserByUsername("ana@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token-generado");

        var request = new RegistroRequest("ana@example.com", "contraseña123", "Ana");
        AuthResponse response = authService().registrar(request);

        assertThat(response).isEqualTo(new AuthResponse("token-generado", "ana@example.com", "Ana"));
        verify(categoriaRepository, times(7)).save(any());
        verify(paqueteService).crearPaqueteConPropietario(eq("Mis tareas"), any(Usuario.class));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getPassword()).isEqualTo("hash-seguro");
    }

    @Test
    void login_devuelveUnTokenCuandoLasCredencialesSonValidas() {
        Usuario usuario = Usuario.builder().id(1L).email("ana@example.com").password("hash").nombre("Ana").build();
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
        UserDetails userDetails = User.builder().username("ana@example.com").password("hash")
                .authorities(java.util.List.of()).build();
        when(userDetailsService.loadUserByUsername("ana@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("token-generado");

        AuthResponse response = authService().login(new LoginRequest("ana@example.com", "contraseña123"));

        assertThat(response).isEqualTo(new AuthResponse("token-generado", "ana@example.com", "Ana"));
        verify(authenticationManager).authenticate(any());
    }
}
