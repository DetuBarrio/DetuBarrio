package detubarrio.rest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import detubarrio.rest.dto.ResenaRequest;
import detubarrio.rest.dto.ResenaResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.Resena;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.ResenaRepository;
import detubarrio.rest.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ComercioRepository comercioRepository;
    private final UsuarioRepository usuarioRepository;

    public List<ResenaResponse> listarResenasPorComercio(Long comercioId) {
        return resenaRepository.findByComercioIdOrderByFechaDesc(comercioId)
            .stream()
            .map(resena -> new ResenaResponse(
                resena.getId(),
                resena.getTitulo(),
                resena.getComentario(),
                resena.getValoracion(),
                resena.getAutorNombre(),
                resena.getAutorEmail(),
                resena.getFecha()
            ))
            .toList();
    }

    @Transactional
    public ResenaResponse crearResena(Long comercioId, ResenaRequest request, String emailUsuario) {
        // 1. Buscamos el comercio
        Comercio comercio = comercioRepository.findById(comercioId)
            .orElseThrow(() -> new EntityNotFoundException("No existe el comercio con id " + comercioId));

        // 2. Obtenemos los datos del usuario logueado
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Construimos la entidad
        Resena resena = Resena.builder()
            .titulo(request.titulo())
            .comentario(request.comentario())
            .valoracion(request.valoracion())
            .autorNombre(usuario.getNombre())
            .autorEmail(usuario.getEmail())
            .fecha(LocalDateTime.now())
            .comercio(comercio)
            .build();

        Resena guardada = resenaRepository.save(resena);

        return new ResenaResponse(
            guardada.getId(),
            guardada.getTitulo(),
            guardada.getComentario(),
            guardada.getValoracion(),
            guardada.getAutorNombre(),
            guardada.getAutorEmail(),
            guardada.getFecha()
        );
    }
}