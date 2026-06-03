package detubarrio.rest.dto; // Asegúrate de que apunte a tu carpeta de DTOs

public interface ClienteProyeccion {
    Long getId();
    String getNombre();
    String getEmail();
    Object getUltimaReserva(); // Usamos Object de forma temporal para evitar conflictos de tipo de fecha
}