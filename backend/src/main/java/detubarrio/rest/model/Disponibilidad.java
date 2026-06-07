package detubarrio.rest.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
    @JoinColumn(name = "comercio_id") // <--- CORREGIDO: Antes estaba como id_comercio
    @JsonIgnore 
    private Comercio comercio;

    private LocalDate fecha; 
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean reservado = false;
}