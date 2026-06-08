package br.com.fiap.orbifreight.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CargaRequestDTO(
        @NotNull(message = "O ID do tipo de carga é obrigatório")
        Long tipoId,

        @NotNull(message = "O ID do veículo é obrigatório")
        Long veiculoId,

        @NotNull(message = "O ID do motorista é obrigatório")
        Long motoristaId,

        // 🟢 ADICIONADO: Validação da Placa do Veículo
        @NotBlank(message = "A placa do veículo é obrigatória")
        String placaVeiculo,

        @NotBlank(message = "O ponto de origem é obrigatório")
        String origem,

        @NotBlank(message = "O ponto de destino é obrigatório")
        String destino,

        @NotNull(message = "A temperatura mínima é obrigatória")
        Double tempMin,

        @NotNull(message = "A temperatura máxima é obrigatória")
        Double tempMax,

        @NotNull(message = "A umidade máxima é obrigatória")
        @Positive(message = "A umidade deve ser um valor positivo")
        Double umidadeMax,

        @NotBlank(message = "O status da carga não pode estar em branco")
        String status
) {}