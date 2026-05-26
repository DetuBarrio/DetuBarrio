package detubarrio.rest.service;

import detubarrio.rest.model.PasswordResetToken;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.PasswordResetTokenRepository;
import detubarrio.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    // 📩 ACCIÓN 1: Generar token y enviar email
    @Transactional
    public void solicitarRecuperacion(String email) {
        // 1. Buscamos si el usuario existe por correo
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("No existe ningún usuario con ese correo electrónico."));

        // 2. Buscamos el token antiguo explícitamente y lo borramos forzando el cambio
        Optional<PasswordResetToken> tokenAntiguo = tokenRepository.findByUsuario(usuario);
        if (tokenAntiguo.isPresent()) {
            tokenRepository.delete(tokenAntiguo.get());
            tokenRepository.flush(); // 🚀 Esto fuerza a la base de datos a borrarlo YA, antes de seguir
        }

        // 3. Generamos un token único (UUID aleatorio)
        String token = UUID.randomUUID().toString();

        // 4. Lo guardamos con una expiración de 15 minutos
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(15));
        tokenRepository.save(resetToken);

        // 5. Enviamos el correo de forma asíncrona
        emailService.enviarEmailRecuperacion(usuario.getEmail(), token);
    }

    // 🔐 ACCIÓN 2: Validar token y cambiar contraseña
    @Transactional
    public void completarRecuperacion(String token, String nuevaContrasena) {
        // 1. Buscamos el token en la base de datos
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("El enlace de recuperación no es válido."));

        // 2. Verificamos si ha caducado
        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El enlace de recuperación ha expirado. Por favor, solicita uno nuevo.");
        }

        // 3. Obtenemos al usuario y actualizamos su contraseña encriptándola
        Usuario usuario = resetToken.getUsuario();
        usuario.setPasswordHash(passwordEncoder.encode(nuevaContrasena)); 
        usuarioRepository.save(usuario);

        // 4. Destruimos el token para que nadie pueda volver a usar este enlace
        tokenRepository.delete(resetToken);
    }
}