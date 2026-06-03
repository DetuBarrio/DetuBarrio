package detubarrio.rest.repository;

import detubarrio.rest.dto.ClienteProyeccion;
import detubarrio.rest.dto.ClienteResumenDTO;
import detubarrio.rest.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    List<Reserva> findByIdComercio(Long idComercio);
    
    List<Reserva> findByIdUsuario(Long idUsuario);

    void deleteByDisponibilidadId(Long idDisponibilidad);

    // 🌟 QUERY OPTIMIZADA: Quitamos el 'new DTO' y usamos alias (AS) claros
    @Query("SELECT u.id AS id, u.nombre AS nombre, u.email AS email, MAX(r.fechaCreacion) AS ultimaReserva " +
           "FROM Reserva r JOIN Usuario u ON r.idUsuario = u.id " + 
           "WHERE r.idComercio = :comercioId AND r.fechaCreacion >= :fechaLimite " +
           "GROUP BY u.id, u.nombre, u.email " +
           "ORDER BY MAX(r.fechaCreacion) DESC")
    List<ClienteProyeccion> findClientesByComercioAndFecha(
            @Param("comercioId") Long comercioId, 
            @Param("fechaLimite") LocalDateTime fechaLimite);

    // Dashboard stats
    long countByIdComercioAndEstadoReserva(Long idComercio, String estadoReserva);

    long countByIdUsuarioAndEstadoReserva(Long idUsuario, String estadoReserva);

    List<Reserva> findTop5ByIdComercioOrderByFechaCreacionDesc(Long idComercio);

    List<Reserva> findTop5ByIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.idComercio = :comercioId AND r.disponibilidad.fecha = :fecha AND r.estadoReserva <> 'CANCELADA'")
    long countReservasHoyByComercio(@Param("comercioId") Long comercioId, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.idUsuario = :usuarioId AND r.fechaCreacion >= :since")
    long countReservasByUsuarioSince(@Param("usuarioId") Long usuarioId, @Param("since") LocalDateTime since);
}