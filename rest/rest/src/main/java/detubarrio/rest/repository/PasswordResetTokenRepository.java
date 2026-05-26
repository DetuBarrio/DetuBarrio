package detubarrio.rest.repository;

import detubarrio.rest.model.PasswordResetToken;
import detubarrio.rest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    // Para buscar el token cuando el usuario vuelva desde el email
    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUsuario(Usuario usuario);
    
    // Para borrar tokens viejos si un usuario pide recuperar la clave varias veces
    void deleteByUsuario(Usuario usuario);
}