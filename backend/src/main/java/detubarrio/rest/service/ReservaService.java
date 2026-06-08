package detubarrio.rest.service;

import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.model.Reserva;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.repository.DisponibilidadRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.repository.ComercioRepository; // Inyectamos este para sacar el nombre del negocio

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

    @Autowired
    private ComercioRepository comercioRepository; // Requerido para saber el nombre en el PDF

    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

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

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 🌟 DISPARO AUTOMÁTICO DEL PDF Y EMAIL TRAS GUARDAR LA RESERVA
        try {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            String nombreComercio = comercioRepository.findById(dto.getIdComercio())
                    .map(c -> c.getNombreComercio()).orElse("Comercio Local");

            if (usuario != null && usuario.getEmail() != null) {
                // 1. Generamos el PDF
                byte[] documentoPdf = pdfService.generarPdfReserva(reservaGuardada, usuario, nombreComercio);
                // 2. Enviamos el email usando hilos paralelos de fondo
                emailService.enviarEmailConPdf(usuario.getEmail(), usuario.getNombre(), nombreComercio, documentoPdf);
            }
        } catch (Exception ex) {
            // Ponemos un trycatch para que si falla el email por mala señal, la reserva en la web NO se rompa
            System.err.println("Alerta: La reserva se creó pero el email falló: " + ex.getMessage());
        }

        return reservaGuardada;
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
        
        if (reserva.getIdComercio() != null) {
            comercioRepository.findById(reserva.getIdComercio()).ifPresent(comercio -> {
                dto.setNombreComercio(comercio.getNombreComercio());
            });
        }
        
        return dto;
    }
}