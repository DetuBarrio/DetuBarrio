package detubarrio.rest.controller;

import detubarrio.rest.model.Reserva;
import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    public Reserva crearReserva(@RequestBody ReservaDTO dto) {
        return reservaService.crearReserva(dto);
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