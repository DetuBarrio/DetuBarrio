package detubarrio.rest.repository;

import java.util.Optional;
import java.util.List; // ✅ Asegúrate de tener este import

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import detubarrio.rest.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "comercio")
    Optional<Usuario> findWithComercioByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByComercioId(Long comercioId);

    @Query("SELECT u FROM Usuario u JOIN u.favoritos f WHERE f.id = :comercioId")
    List<Usuario> findByFavoritoComercioId(@Param("comercioId") Long comercioId);

    @Modifying
    @Query(value = "DELETE FROM cliente_favoritos_comercio WHERE id_comercio = :comercioId", nativeQuery = true)
    void deleteFavoritosByComercioId(@Param("comercioId") Long comercioId);
}