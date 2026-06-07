package detubarrio.rest.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "disponibilidades")
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comercio_id")
    @JsonIgnore 
    private Comercio comercio;

    private LocalDate fecha; 
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean reservado = false;

    // 🛠️ NUEVA RELACIÓN CORREGIDA: Borrado en cascada para limpiar las reservas de esta disponibilidad
    @OneToMany(mappedBy = "disponibilidad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "idDisponibilidad", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Reserva> reservas;
}