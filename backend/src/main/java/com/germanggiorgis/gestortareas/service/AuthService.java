package com.germanggiorgis.gestortareas.service;

import com.germanggiorgis.gestortareas.dto.AuthResponse;
import com.germanggiorgis.gestortareas.dto.LoginRequest;
import com.germanggiorgis.gestortareas.dto.RegistroRequest;
import com.germanggiorgis.gestortareas.model.Categoria;
import com.germanggiorgis.gestortareas.model.Usuario;
import com.germanggiorgis.gestortareas.repository.CategoriaRepository;
import com.germanggiorgis.gestortareas.repository.UsuarioRepository;
import com.germanggiorgis.gestortareas.security.CustomUserDetailsService;
import com.germanggiorgis.gestortareas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final List<String[]> CATEGORIAS_POR_DEFECTO = List.of(
            new String[] { "Doméstico", "#3caf5f" },
            new String[] { "Trabajo", "#3d8bff" },
            new String[] { "Planes", "#a855f7" },
            new String[] { "Trámites", "#ff8a3d" },
            new String[] { "Compromisos", "#e0527a" },
            new String[] { "Salud", "#14b8a6" },
            new String[] { "Finanzas", "#eab308" }
    );

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final PaqueteService paqueteService;

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        Usuario usuario = Usuario.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .build();

        usuarioRepository.save(usuario);
        crearCategoriasPorDefecto(usuario);
        paqueteService.crearPaqueteConPropietario("Mis tareas", usuario);

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre());
    }

    private void crearCategoriasPorDefecto(Usuario usuario) {
        for (String[] categoria : CATEGORIAS_POR_DEFECTO) {
            categoriaRepository.save(Categoria.builder()
                    .nombre(categoria[0])
                    .color(categoria[1])
                    .usuario(usuario)
                    .build());
        }
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(usuario.getEmail()));
        return new AuthResponse(token, usuario.getEmail(), usuario.getNombre());
    }
}
