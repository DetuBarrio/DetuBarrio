package detubarrio.rest.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilidadResponse(
    Long id,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    boolean reservado
) {}