package detubarrio.rest.controller;

import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;

    // Obtener todos los favoritos del usuario autenticado
    @GetMapping
    public ResponseEntity<?> listarFavoritos(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o token no enviado.");
            }
            String email = authentication.getName();
            List<ComercioSummaryResponse> favoritos = favoritoService.listarFavoritosDeUsuario(email);
            return ResponseEntity.ok(favoritos);
        } catch (Exception e) {
            e.printStackTrace(); // 🔴 Esto imprimirá el error exacto en tu consola de Java
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }

    // Añadir o quitar favorito de golpe (Toggle)
    @PostMapping("/{comercioId}")
    public ResponseEntity<?> conmutarFavorito(@PathVariable Long comercioId, Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado o token no enviado.");
            }
            String email = authentication.getName();
            boolean esFavorito = favoritoService.conmutarFavorito(comercioId, email);
            return ResponseEntity.ok(esFavorito);
        } catch (Exception e) {
            e.printStackTrace(); // 🔴 Esto imprimirá el error exacto en tu consola de Java
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno: " + e.getMessage());
        }
    }
}