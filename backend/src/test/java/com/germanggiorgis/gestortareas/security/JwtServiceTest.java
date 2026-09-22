package com.germanggiorgis.gestortareas.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Secreto de prueba: la implementación real toma este mismo valor de la env var jwt.secret.
        jwtService = new JwtService("una-clave-de-prueba-bien-larga-para-hmac-sha256", 3600000L);
    }

    private UserDetails usuario(String email) {
        return User.builder().username(email).password("hash").authorities(java.util.List.of()).build();
    }

    @Test
    void generateToken_incluyeElEmailComoSubject() {
        String token = jwtService.generateToken(usuario("ana@example.com"));

        assertThat(jwtService.extractEmail(token)).isEqualTo("ana@example.com");
    }

    @Test
    void isTokenValid_esTrueParaElMismoUsuario() {
        UserDetails usuario = usuario("ana@example.com");
        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isTokenValid(token, usuario)).isTrue();
    }

    @Test
    void isTokenValid_esFalseParaOtroUsuario() {
        String token = jwtService.generateToken(usuario("ana@example.com"));

        assertThat(jwtService.isTokenValid(token, usuario("otro@example.com"))).isFalse();
    }

    @Test
    void isTokenValid_lanzaExpiredJwtExceptionSiElTokenYaExpiro() {
        // La librería jjwt valida la expiración al parsear el token (dentro de extractEmail), antes de
        // que isTokenValid llegue a su propio chequeo de expiración — por eso un token vencido no
        // devuelve false, tira la excepción acá. JwtAuthFilter la captura (JwtException) y sigue sin
        // autenticar, así que el comportamiento end-to-end es correcto igual.
        JwtService jwtExpirado = new JwtService("una-clave-de-prueba-bien-larga-para-hmac-sha256", -1000L);
        UserDetails usuario = usuario("ana@example.com");
        String token = jwtExpirado.generateToken(usuario);

        assertThatThrownBy(() -> jwtExpirado.isTokenValid(token, usuario))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
