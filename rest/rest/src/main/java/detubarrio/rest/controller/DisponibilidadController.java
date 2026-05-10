package detubarrio.rest.controller;

import detubarrio.rest.dto.DisponibilidadDTO;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.service.DisponibilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/disponibilidad")
@CrossOrigin(origins = "*") // Permite la conexión desde el puerto de Vue (8080, 5173, etc.)
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService service;

    /**
     * Sincroniza la lista de horarios enviada desde el frontend.
     * Transforma el DTO en múltiples registros en la tabla 'disponibilidades'.
     */
    @PostMapping("/configurar")
    public ResponseEntity<?> configurar(@RequestBody DisponibilidadDTO dto) {
        // 1. Validaciones de seguridad
        if (dto == null || dto.getIntervalos() == null || dto.getIntervalos().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "La lista de intervalos está vacía"));
        }

        if (dto.getComercioId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "El ID del comercio es obligatorio"));
        }

        try {
            // 2. Llamada al servicio para persistir en la BD
            // El Service debe usar saveAll para evitar el "machacado" de registros
            service.guardarHorariosDesdeDTO(dto);
            
            return ResponseEntity.ok()
                .body(Map.of("message", "Calendario sincronizado correctamente", "status", "success"));
                
        } catch (Exception e) {
            // En caso de error de SQL o lógica, informamos al frontend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al sincronizar: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los huecos (libres y reservados) de un comercio específico.
     * Útil para pintar el calendario en el lado derecho (40% del ancho).
     */
    @GetMapping("/comercio/{comercioId}")
    public ResponseEntity<List<Disponibilidad>> listarPorComercio(@PathVariable Long comercioId) {
        List<Disponibilidad> disponibilidades = service.obtenerPorComercio(comercioId);
        return ResponseEntity.ok(disponibilidades);
    }

    /**
     * Elimina una disponibilidad específica por su ID.
     * Se usará cuando el usuario pinche en el icono de la papelera de un horario ya guardado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarDisponibilidad(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Horario eliminado con éxito"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se pudo eliminar el registro"));
        }
    }
}