package detubarrio.rest.model; // Asegúrate de que este sea el nombre de tu paquete

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reserva") // El nombre exacto de tu tabla en DBeaver
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    @Column(name = "estado_reserva")
    private String estadoReserva = "CONFIRMADA";

    @Column(name = "id_comercio")
    private Long idComercio;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "id_servicio") // La columna que ya tenías
    private Long idServicio;

    // Relación con el hueco horario de la tabla disponibilidad
    @OneToOne
    @JoinColumn(name = "id_disponibilidad")
    private Disponibilidad disponibilidad;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}