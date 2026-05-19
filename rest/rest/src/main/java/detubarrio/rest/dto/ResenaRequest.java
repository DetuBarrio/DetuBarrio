package detubarrio.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResenaRequest(
    @NotBlank(message = "El título es obligatorio") 
    @Size(max = 80, message = "El título es demasiado largo") 
    String titulo,

    @Size(max = 255, message = "El comentario es demasiado largo") 
    String comentario,

    @NotNull(message = "La valoración es obligatoria")
    @Min(value = 1, message = "La valoración mínima es 1") 
    @Max(value = 5, message = "La valoración máxima es 5") 
    Integer valoracion
) {}