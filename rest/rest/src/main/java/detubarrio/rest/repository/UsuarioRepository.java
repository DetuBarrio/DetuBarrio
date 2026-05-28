package detubarrio.rest.repository;

import java.util.Optional;
import java.util.List; // ✅ Asegúrate de tener este import

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "comercio")
    Optional<Usuario> findWithComercioByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByComercioId(Long comercioId);
}