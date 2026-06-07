package detubarrio.rest.dto;

import java.time.LocalDateTime; // 🌟 Cambiado a LocalDateTime
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResumenDTO {
    private Long id;
    private String nombre;
    private String email;
    private LocalDateTime ultimaReserva; // 🌟 Cambiado a LocalDateTime

    // El constructor ahora recibe LocalDateTime al final
    public ClienteResumenDTO(Long id, String nombre, String email, LocalDateTime ultimaReserva) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.ultimaReserva = ultimaReserva;
    }
}