package br.com.fiap.orbifreight.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TipoCargaRequestDTO(
        @NotBlank(message = "O nome do tipo de carga é obrigatório")
        String nome,

        @NotNull(message = "Temperatura mínima é obrigatória")
        Double tempMin,

        @NotNull(message = "Temperatura máxima é obrigatória")
        Double tempMax,

        @NotNull(message = "Umidade máxima é obrigatória")
        @Positive(message = "Umidade deve ser um valor positivo")
        Double umidadeMax,

        @NotNull(message = "Prazo máximo em horas é obrigatório")
        @Positive(message = "O prazo deve ser positivo")
        Integer prazoMaxHoras
) {}