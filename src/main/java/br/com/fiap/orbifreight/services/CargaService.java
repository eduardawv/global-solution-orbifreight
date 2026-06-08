package br.com.fiap.orbifreight.services;

import br.com.fiap.orbifreight.dtos.CargaRequestDTO;
import br.com.fiap.orbifreight.dtos.CargaResponseDTO;
import br.com.fiap.orbifreight.models.Carga;
import br.com.fiap.orbifreight.models.TipoCarga;
import br.com.fiap.orbifreight.repositories.CargaRepository;
import br.com.fiap.orbifreight.repositories.TipoCargaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargaService {

    @Autowired
    private CargaRepository cargaRepository;

    @Autowired
    private TipoCargaRepository tipoCargaRepository;

    public CargaResponseDTO salvar(CargaRequestDTO request) {
        TipoCarga tipo = tipoCargaRepository.findById(request.tipoId())
                .orElseThrow(() -> new RuntimeException("Tipo de Carga não encontrado com o ID: " + request.tipoId()));

        Carga carga = new Carga();
        carga.setTipoCarga(tipo);
        carga.setVeiculoId(request.veiculoId());
        carga.setMotoristaId(request.motoristaId());
        carga.setPlacaVeiculo(request.placaVeiculo()); // 🟢 SALVA A PLACA
        carga.setOrigem(request.origem());
        carga.setDestino(request.destino());
        carga.setTempMin(request.tempMin());
        carga.setTempMax(request.tempMax());
        carga.setUmidadeMax(request.umidadeMax());
        carga.setStatus(request.status());

        Carga cargaSalva = cargaRepository.save(carga);
        return converterParaDTO(cargaSalva);
    }

    public List<CargaResponseDTO> listarTodas() {
        return cargaRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public CargaResponseDTO buscarPorId(Long id) {
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carga não encontrada com o ID: " + id));

        return converterParaDTO(carga);
    }

    public CargaResponseDTO atualizar(Long id, CargaRequestDTO request) {
        Carga carga = cargaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carga não encontrada com o ID: " + id));

        TipoCarga tipo = tipoCargaRepository.findById(request.tipoId())
                .orElseThrow(() -> new RuntimeException("Tipo de Carga não encontrado com o ID: " + request.tipoId()));

        carga.setTipoCarga(tipo);
        carga.setVeiculoId(request.veiculoId());
        carga.setMotoristaId(request.motoristaId());
        carga.setPlacaVeiculo(request.placaVeiculo()); // 🟢 ATUALIZA A PLACA
        carga.setOrigem(request.origem());
        carga.setDestino(request.destino());
        carga.setTempMin(request.tempMin());
        carga.setTempMax(request.tempMax());
        carga.setUmidadeMax(request.umidadeMax());
        carga.setStatus(request.status());

        Carga cargaAtualizada = cargaRepository.save(carga);
        return converterParaDTO(cargaAtualizada);
    }

    public void excluir(Long id) {
        if (!cargaRepository.existsById(id)) {
            throw new RuntimeException("Carga não encontrada com o ID: " + id);
        }
        cargaRepository.deleteById(id);
    }

    private CargaResponseDTO converterParaDTO(Carga carga) {
        // 🟢 ORDEM E TIPAGEM ALINHADAS 100% COM O RECORD DTO
        return new CargaResponseDTO(
                carga.getId(),
                carga.getTipoCarga().getId(),
                carga.getVeiculoId(),
                carga.getMotoristaId(),
                carga.getPlacaVeiculo(), // 5. String
                carga.getOrigem(),       // 6. String
                carga.getDestino(),      // 7. String
                carga.getTempMin(),      // 8. Double
                carga.getTempMax(),      // 9. Double
                carga.getUmidadeMax(),   // 10. Double
                carga.getStatus()        // 11. String
        );
    }
}