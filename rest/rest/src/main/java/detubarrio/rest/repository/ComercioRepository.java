package detubarrio.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // AÑADIR
import org.springframework.data.repository.query.Param; // AÑADIR

import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.EstadoComercio;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    
    // ESTE ES EL MÉTODO CLAVE: Carga el comercio y sus disponibilidades en una sola consulta
    @Query("SELECT c FROM Comercio c LEFT JOIN FETCH c.disponibilidades WHERE c.id = :id")
    Optional<Comercio> findByIdWithDisponibilidades(@Param("id") Long id);

    // A
    // --- TUS MÉTODOS EXISTENTES ---
    Optional<Comercio> findByUsuarioCreadorId(Long usuarioId);
    List<Comercio> findByCategoriaId(Long categoriaId);
    List<Comercio> findByEstado(EstadoComercio estado);
    List<Comercio> findByCategoriaIdAndEstado(Long categoriaId, EstadoComercio estado);
    List<Comercio> findByEstadoAndGestionAutorizadaTrue(EstadoComercio estado);
    List<Comercio> findByCategoriaIdAndEstadoAndGestionAutorizadaTrue(Long categoriaId, EstadoComercio estado);
}