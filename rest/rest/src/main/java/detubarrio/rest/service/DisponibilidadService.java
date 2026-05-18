package detubarrio.rest.service;

import detubarrio.rest.dto.DisponibilidadDTO;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.Disponibilidad;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.DisponibilidadRepository;
import detubarrio.rest.repository.ReservaRepository;

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

    @Autowired
    private ComercioRepository comercioRepository;

    @Autowired // <-- 2. INYECTA EL REPOSITORIO DE RESERVAS
    private ReservaRepository reservaRepository;

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
            // BUSCA EL COMERCIO PRIMERO (Esto arregla el error rojo de la imagen)
            Comercio comercio = comercioRepository.findById(dto.getComercioId())
                .orElseThrow(() -> new RuntimeException("Comercio no encontrado"));

            nuevaDisp.setComercio(comercio); // Usa el objeto, no solo el ID
            nuevaDisp.setFecha(fechaNueva);
            nuevaDisp.setHoraInicio(inicioNuevo);
            nuevaDisp.setHoraFin(finNuevo);
            nuevaDisp.setReservado(false);

            repository.save(nuevaDisp);
        }
    }


    @Transactional
    public void eliminar(Long id) {
        if (repository.existsById(id)) {
            
            reservaRepository.deleteByDisponibilidadId(id); 
            
            repository.deleteById(id);
            
        } else {
            throw new RuntimeException("No se encontró la disponibilidad con ID: " + id);
        }
    }

        public List<Disponibilidad> obtenerPorComercio(Long comercioId) {
            return repository.findByComercioId(comercioId);
        }
    }