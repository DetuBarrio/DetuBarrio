package detubarrio.rest.model;

import jakarta.persistence.*;
import java.time.LocalDate; // Importante para la fecha
import java.time.LocalTime;
import lombok.Data;

@Entity
@Data // Esto genera getters y setters automáticamente
@Table(name = "disponibilidades")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long comercioId;

    // CAMBIO AQUÍ: De String a LocalDate
    private LocalDate fecha; 

    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean reservado = false;
}