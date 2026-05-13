package detubarrio.rest.dto;

import lombok.Data;

@Data
public class ReservaDTO {
    private Long idUsuario;
    private Long idComercio;
    private Long idDisponibilidad;
    private Long idServicio; // Por si lo usas en el futuro
}