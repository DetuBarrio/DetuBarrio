package detubarrio.rest.service;

import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.model.Reserva;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.repository.DisponibilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Transactional
    public Reserva crearReserva(ReservaDTO dto) {
        // 1. Buscar el hueco horario
        Disponibilidad disp = disponibilidadRepository.findById(dto.getIdDisponibilidad())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        // 2. Marcarlo como reservado
        disp.setReservado(true);
        disponibilidadRepository.save(disp);

        // 3. Crear y guardar la reserva
        Reserva reserva = new Reserva();
        reserva.setIdUsuario(dto.getIdUsuario());
        reserva.setIdComercio(dto.getIdComercio());
        reserva.setDisponibilidad(disp);
        reserva.setIdServicio(dto.getIdServicio());
        reserva.setEstadoReserva("CONFIRMADA");

        return reservaRepository.save(reserva);
    }
}