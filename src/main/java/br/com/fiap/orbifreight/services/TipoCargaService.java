package br.com.fiap.orbifreight.services;

import br.com.fiap.orbifreight.dtos.TipoCargaRequestDTO;
import br.com.fiap.orbifreight.dtos.TipoCargaResponseDTO;
import br.com.fiap.orbifreight.models.TipoCarga;
import br.com.fiap.orbifreight.repositories.TipoCargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoCargaService {

    @Autowired
    private TipoCargaRepository tipoCargaRepository;

    public TipoCargaResponseDTO salvar(TipoCargaRequestDTO request) {
        TipoCarga tipo = new TipoCarga();
        tipo.setNome(request.nome());
        tipo.setTempMin(request.tempMin());
        tipo.setTempMax(request.tempMax());
        tipo.setUmidadeMax(request.umidadeMax());
        tipo.setPrazoMaxHoras(request.prazoMaxHoras());

        return converterParaDTO(tipoCargaRepository.save(tipo));
    }

    public List<TipoCargaResponseDTO> listarTodos() {
        return tipoCargaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public TipoCargaResponseDTO buscarPorId(Long id) {
        TipoCarga tipo = tipoCargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Carga não encontrado com o ID: " + id));
        return converterParaDTO(tipo);
    }

    public TipoCargaResponseDTO atualizar(Long id, TipoCargaRequestDTO request) {
        TipoCarga tipo = tipoCargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Carga não encontrado com o ID: " + id));

        tipo.setNome(request.nome());
        tipo.setTempMin(request.tempMin());
        tipo.setTempMax(request.tempMax());
        tipo.setUmidadeMax(request.umidadeMax());
        tipo.setPrazoMaxHoras(request.prazoMaxHoras());

        return converterParaDTO(tipoCargaRepository.save(tipo));
    }

    public void excluir(Long id) {
        if (!tipoCargaRepository.existsById(id)) {
            throw new RuntimeException("Tipo de Carga não encontrado com o ID: " + id);
        }
        tipoCargaRepository.deleteById(id);
    }

    private TipoCargaResponseDTO converterParaDTO(TipoCarga tipo) {
        return new TipoCargaResponseDTO(
                tipo.getId(),
                tipo.getNome(),
                tipo.getTempMin(),
                tipo.getTempMax(),
                tipo.getUmidadeMax(),
                tipo.getPrazoMaxHoras()
        );
    }
}