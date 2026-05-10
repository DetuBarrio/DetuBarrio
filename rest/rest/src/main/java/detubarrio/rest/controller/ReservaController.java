package detubarrio.rest.controller;

import detubarrio.rest.model.Reserva;
import detubarrio.rest.dto.ReservaDTO;
import detubarrio.rest.repository.ReservaRepository;
import detubarrio.rest.repository.DisponibilidadRepository;
import detubarrio.rest.service.ReservaService; // Importante añadir este import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*") // Para que Vue pueda conectar sin problemas
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private ReservaService reservaService; // Inyectamos el servicio para usar su lógica

    // 1. Crear una nueva reserva (El vecino elige un hueco)
    @PostMapping("/crear")
    public Reserva crearReserva(@RequestBody ReservaDTO dto) {
        // Ahora llamamos al servicio que gestiona la lógica de marcar como 'reservado'
        return reservaService.crearReserva(dto);
    }

    // 2. Obtener las reservas de un comercio (Para la agenda de Paqui)
    @GetMapping("/comercio/{id}")
    public List<Reserva> obtenerPorComercio(@PathVariable Long id) {
        return reservaRepository.findByIdComercio(id);
    }
}