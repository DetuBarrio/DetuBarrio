package detubarrio.rest.service;

import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.model.Reserva;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.repository.DisponibilidadRepository;
import detubarrio.rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Reserva crearReserva(ReservaDTO dto) {
        Disponibilidad disp = disponibilidadRepository.findById(dto.getIdDisponibilidad())
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        disp.setReservado(true);
        disponibilidadRepository.save(disp);

        Reserva reserva = new Reserva();
        reserva.setIdUsuario(dto.getIdUsuario());
        reserva.setIdComercio(dto.getIdComercio());
        reserva.setDisponibilidad(disp);
        reserva.setIdServicio(dto.getIdServicio());
        reserva.setEstadoReserva("CONFIRMADA");

        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarReservasPorComercio(Long idComercio) {
        return reservaRepository.findByIdComercio(idComercio).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservaDTO> listarReservasPorUsuario(Long idUsuario) {
        return reservaRepository.findByIdUsuario(idUsuario).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelarReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva no localizada"));
        
        reserva.setEstadoReserva("CANCELADA");
        reservaRepository.save(reserva);

        // Liberamos el hueco horario para que otra persona pueda reservar
        if (reserva.getDisponibilidad() != null) {
            Disponibilidad disp = reserva.getDisponibilidad();
            disp.setReservado(false);
            disponibilidadRepository.save(disp);
        }
    }

    private ReservaDTO toDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setIdUsuario(reserva.getIdUsuario());
        dto.setIdComercio(reserva.getIdComercio());
        dto.setIdServicio(reserva.getIdServicio());
        
        // 🛠️ Ahora sí compilará perfectamente usando el campo estándar de tu entidad:
        dto.setEstadoReserva(reserva.getEstadoReserva());
        
        if (reserva.getDisponibilidad() != null) {
            dto.setIdDisponibilidad(reserva.getDisponibilidad().getId());
            dto.setDisponibilidad(reserva.getDisponibilidad());
        }
        
        if (reserva.getIdUsuario() != null) {
            usuarioRepository.findById(reserva.getIdUsuario()).ifPresent(usuario -> {
                dto.setClienteNombre(usuario.getNombre());
                dto.setClienteEmail(usuario.getEmail());
            });
        }
        
        return dto;
    }
}