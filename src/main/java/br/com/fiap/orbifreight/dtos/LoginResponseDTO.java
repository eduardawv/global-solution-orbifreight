package br.com.fiap.orbifreight.dtos;

public record LoginResponseDTO(
        String token,
        Long id,
        String nome
) {}