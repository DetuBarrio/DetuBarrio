package detubarrio.rest.controller;

import detubarrio.rest.dto.ClienteProyeccion;
import detubarrio.rest.repository.ReservaRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // 🌟 Cambiado a LocalDateTime
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/comercio/{comercioId}")
    public ResponseEntity<List<ClienteProyeccion>> listarPorComercio(
            @PathVariable Long comercioId,
            @RequestParam(defaultValue = "todos") String filtro) {
        
        LocalDateTime fechaLimite = null; // 🌟 Ahora es LocalDateTime
        LocalDateTime ahora = LocalDateTime.now();

        switch (filtro) {
            case "dia":
                fechaLimite = ahora.minusDays(1);
                break;
            case "mes":
                fechaLimite = ahora.minusMonths(1);
                break;
            case "anio":
                fechaLimite = ahora.minusYears(1);
                break;
            default:
                // Historial completo: desde el año 2000 a las 00:00
                fechaLimite = LocalDate.of(2000, 1, 1).atStartOfDay(); 
                break;
        }

        List<ClienteProyeccion> clientes = reservaRepository.findClientesByComercioAndFecha(comercioId, fechaLimite);
        
        return ResponseEntity.ok(clientes);
    }
}