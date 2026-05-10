package detubarrio.rest.repository;

import java.util.List;
import java.util.Optional; // Importación necesaria para el nuevo método

import org.springframework.data.jpa.repository.JpaRepository;

import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.EstadoComercio;

public interface ComercioRepository extends JpaRepository<Comercio, Long> {
    
    // --- NUEVO MÉTODO PARA VINCULAR USUARIO Y COMERCIO ---
    Optional<Comercio> findByUsuarioCreadorId(Long usuarioId);
    
    // --- TUS MÉTODOS EXISTENTES ---
    List<Comercio> findByCategoriaId(Long categoriaId);
    
    List<Comercio> findByEstado(EstadoComercio estado);
    
    List<Comercio> findByCategoriaIdAndEstado(Long categoriaId, EstadoComercio estado);

    List<Comercio> findByEstadoAndGestionAutorizadaTrue(EstadoComercio estado);

    List<Comercio> findByCategoriaIdAndEstadoAndGestionAutorizadaTrue(Long categoriaId, EstadoComercio estado);
}