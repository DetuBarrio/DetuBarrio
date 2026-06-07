package detubarrio.rest.dto;

import java.util.List;

public class DisponibilidadDTO {
    private Long comercioId;
    private List<IntervaloDTO> intervalos;

    // Getters y Setters
    public Long getComercioId() { return comercioId; }
    public void setComercioId(Long comercioId) { this.comercioId = comercioId; }

    public List<IntervaloDTO> getIntervalos() { return intervalos; }
    public void setIntervalos(List<IntervaloDTO> intervalos) { this.intervalos = intervalos; }

    // Clase interna para los rangos horarios
    public static class IntervaloDTO {
        // CAMBIO: Usamos 'fecha' en lugar de 'dia' para recibir "2026-05-21"
        private String fecha; 
        private String inicio;
        private String fin;

        // Getters y Setters actualizados
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }

        public String getInicio() { return inicio; }
        public void setInicio(String inicio) { this.inicio = inicio; }

        public String getFin() { return fin; }
        public void setFin(String fin) { this.fin = fin; }
    }
}