package detubarrio.rest.service;

import detubarrio.rest.dto.ComercioSummaryResponse;
import detubarrio.rest.model.Comercio;
import detubarrio.rest.model.Usuario;
import detubarrio.rest.repository.ComercioRepository;
import detubarrio.rest.repository.UsuarioRepository;
import detubarrio.rest.repository.ResenaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ComercioRepository comercioRepository;
    @Autowired private ResenaRepository resenaRepository;

    @Transactional
    public boolean conmutarFavorito(Long comercioId, String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Comercio comercio = comercioRepository.findById(comercioId)
                .orElseThrow(() -> new EntityNotFoundException("Comercio no encontrado"));

        if (usuario.getFavoritos().contains(comercio)) {
            usuario.getFavoritos().remove(comercio);
            return false; // Se ha quitado
        } else {
            usuario.getFavoritos().add(comercio);
            return true; // Se ha añadido
        }
    }

    @Transactional(readOnly = true)
    public List<ComercioSummaryResponse> listarFavoritosDeUsuario(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        return usuario.getFavoritos().stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    private ComercioSummaryResponse toSummaryResponse(Comercio c) {
        Double media = resenaRepository.findAverageValoracionByComercioId(c.getId());
        Long total = resenaRepository.countByComercioId(c.getId());

        return new ComercioSummaryResponse(
            c.getId(),
            c.getNombreComercio(),
            c.getDescripcion(),
            c.getHorario(),
            c.getDiasApertura(),
            c.getLogo(),
            c.getCategoria() != null ? c.getCategoria().getNombreCategoria() : "Sin categoría",
            media != null ? media : 0.0,
            total != null ? total : 0L
        );
    }
}