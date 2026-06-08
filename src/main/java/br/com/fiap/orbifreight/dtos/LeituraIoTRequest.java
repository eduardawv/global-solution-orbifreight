package br.com.fiap.orbifreight.dtos;

public record LeituraIoTRequest(
        Long carga_id,
        Double temperatura,
        Double umidade,
        String risco
) {}