package detubarrio.rest.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.Resena;
import detubarrio.rest.model.Reserva;
import detubarrio.rest.model.RolUsuario;
import detubarrio.rest.model.EstadoComercio;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ResenaRepository;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.repository.SolicitudColaboracionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioRepository usuarioRepository;
    private final ComercioRepository comercioRepository;
    private final SolicitudColaboracionRepository solicitudColaboracionRepository;
    private final ReservaRepository reservaRepository;
    private final ResenaRepository resenaRepository;

    private Usuario loadCurrentUser(String email) {
        return usuarioRepository.findWithComercioByEmailIgnoreCase(email)
            .orElseThrow(() -> new EntityNotFoundException("No existe el usuario autenticado"));
    }

    @GetMapping("/usuario")
    @Transactional(readOnly = true)
    public Map<String, Object> usuario(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.USUARIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para usuarios");
        }

        long reservasActivas = reservaRepository.countByIdUsuarioAndEstadoReserva(usuario.getId(), "CONFIRMADA");
        int favoritosCount = usuario.getFavoritos() != null ? usuario.getFavoritos().size() : 0;
        long reservasEstaSemana = reservaRepository.countReservasByUsuarioSince(usuario.getId(), LocalDateTime.now().minusDays(7));
        List<Map<String, Object>> ultimasReservas = buildReservasList(reservaRepository.findTop5ByIdUsuarioOrderByFechaCreacionDesc(usuario.getId()));

        Map<String, Object> response = new HashMap<>();
        response.put("nombre", usuario.getNombre());
        response.put("rol", usuario.getRol().name());
        response.put("email", usuario.getEmail());
        response.put("reservasActivas", reservasActivas);
        response.put("favoritosCount", favoritosCount);
        response.put("reservasEstaSemana", reservasEstaSemana);
        response.put("ultimasReservas", ultimasReservas);
        return response;
    }

    @GetMapping("/comercio")
    @Transactional(readOnly = true)
    public Map<String, Object> comercio(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.COMERCIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para comercios");
        }

        Comercio comercio = usuario.getComercio();
        String nombreComercio = comercio != null ? comercio.getNombreComercio() : "Sin comercio asignado";
        String estadoComercio = comercio != null ? comercio.getEstado().name() : "PENDIENTE";
        boolean gestionAutorizada = comercio != null && comercio.isGestionAutorizada();
        String motivoRechazo = comercio != null ? comercio.getMotivoRechazo() : null;
        String motivoBloqueoGestion = comercio != null ? comercio.getMotivoBloqueoGestion() : null;
        var solicitudColaboracion = usuario.getEmail() != null
            ? solicitudColaboracionRepository.findTopByEmailComercioIgnoreCaseOrderByFechaCreacionDesc(usuario.getEmail()).orElse(null)
            : null;

        Map<String, Object> response = new HashMap<>();
        response.put("nombre", usuario.getNombre());
        response.put("rol", usuario.getRol().name());
        response.put("email", usuario.getEmail());
        response.put("comercioNombre", nombreComercio);
        response.put("estadoComercio", estadoComercio);
        response.put("gestionAutorizada", gestionAutorizada);
        response.put("motivoRechazo", motivoRechazo == null ? "" : motivoRechazo);
        response.put("motivoBloqueoGestion", motivoBloqueoGestion == null ? "" : motivoBloqueoGestion);
        response.put("solicitudColaboracionEstado", solicitudColaboracion != null ? solicitudColaboracion.getEstado().name() : "SIN_SOLICITUD");
        response.put("solicitudColaboracionMotivo", solicitudColaboracion != null && solicitudColaboracion.getMotivoRechazo() != null ? solicitudColaboracion.getMotivoRechazo() : "");

        if (comercio != null) {
            Long idComercio = comercio.getId();
            long reservasHoy = reservaRepository.countReservasHoyByComercio(idComercio, LocalDate.now());
            long totalResenas = resenaRepository.countByComercioId(idComercio);
            Double mediaValoracion = resenaRepository.findAverageValoracionByComercioId(idComercio);
            long reservasActivas = reservaRepository.countByIdComercioAndEstadoReserva(idComercio, "CONFIRMADA");
            List<Map<String, Object>> ultimasReservas = buildReservasList(reservaRepository.findTop5ByIdComercioOrderByFechaCreacionDesc(idComercio));

            response.put("reservasHoy", reservasHoy);
            response.put("totalResenas", totalResenas);
            response.put("mediaValoracion", mediaValoracion != null ? mediaValoracion : 0.0);
            response.put("reservasActivas", reservasActivas);
            response.put("ultimasReservas", ultimasReservas);
        } else {
            response.put("reservasHoy", 0);
            response.put("totalResenas", 0);
            response.put("mediaValoracion", 0.0);
            response.put("reservasActivas", 0);
            response.put("ultimasReservas", List.of());
        }

        return response;
    }

    private List<Map<String, Object>> buildReservasList(List<Reserva> reservas) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Reserva r : reservas) {
            String comercioNombre = comercioRepository.findById(r.getIdComercio())
                .map(Comercio::getNombreComercio)
                .orElse("Comercio #" + r.getIdComercio());
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("comercioNombre", comercioNombre);
            item.put("estadoReserva", r.getEstadoReserva());
            item.put("fechaReserva", r.getDisponibilidad() != null ? r.getDisponibilidad().getFecha().toString() : null);
            item.put("horaInicio", r.getDisponibilidad() != null ? r.getDisponibilidad().getHoraInicio().toString() : null);
            item.put("horaFin", r.getDisponibilidad() != null ? r.getDisponibilidad().getHoraFin().toString() : null);
            item.put("fechaCreacion", r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null);
            item.put("idServicio", r.getIdServicio());
            item.put("idUsuario", r.getIdUsuario());
            list.add(item);
        }
        return list;
    }

    @DeleteMapping("/comercio")
    @Transactional
    public Map<String, String> eliminarComercioRechazado(Authentication authentication) {
        Usuario usuario = loadCurrentUser(authentication.getName());

        if (usuario.getRol() != RolUsuario.COMERCIO) {
            throw new IllegalArgumentException("Este endpoint solo está disponible para comercios");
        }

        Comercio comercio = usuario.getComercio();
        if (comercio == null || comercio.getEstado() != EstadoComercio.RECHAZADO) {
            throw new IllegalStateException("Solo se puede eliminar un comercio rechazado");
        }

        usuarioRepository.deleteFavoritosByComercioId(comercio.getId());

        comercio.setUsuarioCreador(null);
        comercioRepository.save(comercio);

        usuario.setComercio(null);
        usuarioRepository.save(usuario);

        comercioRepository.delete(comercio);
        usuarioRepository.delete(usuario);

        return Map.of("message", "Comercio y usuario eliminados correctamente");
    }
}
