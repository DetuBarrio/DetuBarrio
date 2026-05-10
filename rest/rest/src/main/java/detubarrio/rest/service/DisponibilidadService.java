package detubarrio.rest.service;

import detubarrio.rest.dto.DisponibilidadDTO;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.repository.DisponibilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DisponibilidadService {

    @Autowired
    private DisponibilidadRepository repository;

    @Transactional
    public void guardarHorariosDesdeDTO(DisponibilidadDTO dto) {
        for (DisponibilidadDTO.IntervaloDTO intervalo : dto.getIntervalos()) {
            LocalDate fechaNueva = LocalDate.parse(intervalo.getFecha());
            LocalTime inicioNuevo = LocalTime.parse(intervalo.getInicio());
            LocalTime finNuevo = LocalTime.parse(intervalo.getFin());

            // 1. Buscamos horarios existentes para ese comercio y esa fecha
            List<Disponibilidad> existentes = repository.findByComercioIdAndFecha(dto.getComercioId(), fechaNueva);

            // 2. Validar solapamiento
            for (Disponibilidad disp : existentes) {
                // Un horario solapa si: (InicioNuevo < FinExistente) Y (FinNuevo > InicioExistente)
                if (inicioNuevo.isBefore(disp.getHoraFin()) && finNuevo.isAfter(disp.getHoraInicio())) {
                    throw new RuntimeException("El horario " + inicioNuevo + "-" + finNuevo + 
                        " se solapa con uno existente (" + disp.getHoraInicio() + "-" + disp.getHoraFin() + ")");
                }
            }

            // 3. Si pasa la validación, guardamos
            Disponibilidad nuevaDisp = new Disponibilidad();
            nuevaDisp.setComercioId(dto.getComercioId());
            nuevaDisp.setFecha(fechaNueva);
            nuevaDisp.setHoraInicio(inicioNuevo);
            nuevaDisp.setHoraFin(finNuevo);
            nuevaDisp.setReservado(false);
            
            repository.save(nuevaDisp);
        }
    }

    public void eliminar(Long id) {
        // Verificamos si existe antes de borrar para evitar errores
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la disponibilidad con ID: " + id);
        }
    }   

    public List<Disponibilidad> obtenerPorComercio(Long comercioId) {
        return repository.findByComercioId(comercioId);
    }
}