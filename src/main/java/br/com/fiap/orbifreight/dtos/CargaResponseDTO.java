package br.com.fiap.orbifreight.dtos;

public record CargaResponseDTO(
        Long id,
        Long tipoId,
        Long veiculoId,
        Long motoristaId,
        String placaVeiculo, // 🟢 Posição 5: String
        String origem,       // 🟢 Posição 6: String
        String destino,      // 🟢 Posição 7: String
        Double tempMin,      // Posição 8: Double
        Double tempMax,      // Posição 9: Double
        Double umidadeMax,   // Posição 10: Double
        String status        // Posição 11: String
) {}