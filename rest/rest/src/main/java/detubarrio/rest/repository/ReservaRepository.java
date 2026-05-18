package detubarrio.rest.repository;

import detubarrio.rest.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Para que el Comercio vea sus reservas recibidas
    List<Reserva> findByIdComercio(Long idComercio);
    
    // Para que el Cliente vea sus reservas solicitadas
    List<Reserva> findByIdUsuario(Long idUsuario);

    void deleteByDisponibilidadId(Long idDisponibilidad);
}