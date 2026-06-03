package detubarrio.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.dto.ActualizarPerfilRequest;
import detubarrio.rest.dto.AuthLoginRequest;
import detubarrio.rest.dto.AuthRegisterRequest;
import detubarrio.rest.dto.AuthResponse;
import detubarrio.rest.dto.CambiarContrasenaRequest;
import detubarrio.rest.dto.ForgotPasswordRequest;
import detubarrio.rest.dto.ResetPasswordRequest;
import detubarrio.rest.dto.UsuarioMeResponse;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.service.AuthService;
import detubarrio.rest.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioMeResponse me(Authentication authentication) {
        return authService.meByEmail(authentication.getName());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarRecuperacion(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            usuarioService.solicitarRecuperacion(request.getEmail());
            // Devolvemos un JSON genérico para dar buena experiencia en el front
            return ResponseEntity.ok(Map.of("message", "Si el correo existe, se ha enviado un enlace de recuperación."));
        } catch (Exception e) {
            // Por seguridad, puedes devolver el mismo mensaje o capturar el error
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Petición final para cambiar la contraseña usando el token
    @PostMapping("/reset-password")
    public ResponseEntity<?> completarRecuperacion(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            usuarioService.completarRecuperacion(request.getToken(), request.getNuevaContrasena());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente. ¡Ya puedes iniciar sesión!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> actualizarPerfil(Authentication authentication, @Valid @RequestBody ActualizarPerfilRequest request) {
        try {
            Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            Usuario actualizado = usuarioService.actualizarPerfil(usuario.getId(), request.getNombre(), request.getEmail());

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("message", "Perfil actualizado correctamente");
            response.put("nombre", actualizado.getNombre());
            response.put("email", actualizado.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> cambiarContrasena(Authentication authentication, @Valid @RequestBody CambiarContrasenaRequest request) {
        try {
            if (!request.getNuevaContrasena().equals(request.getConfirmarContrasena())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Las contraseñas no coinciden"));
            }

            Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuarioService.cambiarContrasena(usuario.getId(), request.getContrasenaActual(), request.getNuevaContrasena());
            return ResponseEntity.ok(Map.of("message", "Contraseña cambiada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> eliminarCuenta(Authentication authentication) {
        try {
            Usuario usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuarioService.eliminarCuenta(usuario.getId());
            return ResponseEntity.ok(Map.of("message", "Cuenta eliminada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
