package br.com.fiap.orbifreight.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertaRequestDTO(
        @NotNull(message = "O ID da carga é obrigatório")
        Long cargaId,

        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @NotBlank(message = "A descrição é obrigatória")
        String descricao,

        @NotBlank(message = "O nível do alerta é obrigatório")
        String nivel,

        @NotBlank(message = "O status do alerta é obrigatório")
        String status
) {}