package detubarrio.rest.dto;

import lombok.Data;
import detubarrio.rest.model.Disponibilidad; // <--- Asegúrate de importar tu modelo

@Data
public class ReservaDTO {
    private Long id;
    private Long idUsuario;
    private Long idComercio;
    private Long idServicio;
    private String clienteNombre;
    private String clienteEmail;
    private String nombreComercio;
    
    private Long idDisponibilidad; 
    private Disponibilidad disponibilidad; 
    private String estadoReserva;
}