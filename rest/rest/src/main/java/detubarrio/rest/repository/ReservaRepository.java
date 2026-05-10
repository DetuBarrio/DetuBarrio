package detubarrio.rest.repository;

import detubarrio.rest.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Para que Paqui vea sus reservas
    List<Reserva> findByIdComercio(Long idComercio);
    
    // Para que el cliente vea sus propias reservas
    List<Reserva> findByIdUsuario(Long idUsuario);
}