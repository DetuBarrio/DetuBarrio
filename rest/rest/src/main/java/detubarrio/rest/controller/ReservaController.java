package detubarrio.rest.controller;

import detubarrio.rest.model.Reserva;
import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public ResponseEntity<?> crearReserva(
            @RequestHeader(value = "Authorization", required = false) String tokenHeader,
            @RequestBody ReservaDTO dto) {
        
        // 1. 🛡️ FILTRO DE SEGURIDAD: Evita que usuarios no autenticados (sin token) creen registros
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ Intento de reserva bloqueado: Cabecera Authorization ausente o inválida.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Para reservar citas tienes que logearte primero."));
        }

        // 2. Validación de datos mínimos
        if (dto.getIdUsuario() == null || dto.getIdDisponibilidad() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Datos de reserva incompletos (Falta Usuario o Disponibilidad)."));
        }

        try {
            // Si pasa el filtro, procesamos la reserva con normalidad
            Reserva nuevaReserva = reservaService.crearReserva(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "No se pudo procesar la reserva: " + e.getMessage()));
        }
    }

    @GetMapping("/comercio/{id}")
    public List<ReservaDTO> obtenerPorComercio(@PathVariable Long id) {
        List<ReservaDTO> lista = reservaService.listarReservasPorComercio(id);
        System.out.println("Reservas encontradas para comercio " + id + ": " + lista.size());
        return lista;
    }

    // 🚀 NUEVO ENDPOINT: Resuelve el Error 500 al cargar las citas del cliente conectado
    @GetMapping("/usuario/{id}")
    public List<ReservaDTO> obtenerPorUsuario(@PathVariable Long id) {
        List<ReservaDTO> lista = reservaService.listarReservasPorUsuario(id);
        System.out.println("Reservas encontradas para usuario " + id + ": " + lista.size());
        return lista;
    }

    // ❌ NUEVO ENDPOINT: Permite al usuario o comercio anular la cita desde la interfaz
    @PutMapping("/{id}/cancelar")
    public void cancelarCita(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        System.out.println("Reserva con ID " + id + " marcada como CANCELADA de forma correcta.");
    }

    @DeleteMapping("/{id}")
    public void borrarReserva(@PathVariable Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new RuntimeException("La reserva no existe");
        }
        reservaRepository.deleteById(id);
        System.out.println("Reserva con ID " + id + " eliminada permanentemente.");
    }
}