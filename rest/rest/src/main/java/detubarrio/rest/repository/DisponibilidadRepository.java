package detubarrio.rest.repository;

import detubarrio.rest.model.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByComercioId(Long comercioId);
    
    // Añade esta línea:
    List<Disponibilidad> findByComercioIdAndFecha(Long comercioId, LocalDate fecha);
}