package br.com.fiap.orbifreight.dtos;

public record TipoCargaResponseDTO(
        Long id,
        String nome,
        Double tempMin,
        Double tempMax,
        Double umidadeMax,
        Integer prazoMaxHoras
) {}