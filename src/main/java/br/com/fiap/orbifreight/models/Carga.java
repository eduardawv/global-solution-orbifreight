package br.com.fiap.orbifreight.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "CARGA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoCarga tipoCarga;

    @Column(name = "veiculo_id", nullable = false)
    private Long veiculoId;

    @Column(name = "motorista_id", nullable = false)
    private Long motoristaId;

    // 🟢 NOVA COLUNA PARA A PLACA
    @Column(name = "placa_veiculo", length = 10, nullable = false)
    private String placaVeiculo;

    @Column(name = "origem", length = 150, nullable = false)
    private String origem;

    @Column(name = "destino", length = 150, nullable = false)
    private String destino;

    @Column(name = "temp_min", nullable = false)
    private Double tempMin;

    @Column(name = "temp_max", nullable = false)
    private Double tempMax;

    @Column(name = "umidade_max", nullable = false)
    private Double umidadeMax;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    // Isso avisa ao banco: "Pode apagar os alertas históricos junto com a carga!"
    @OneToMany(mappedBy = "carga", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alerta> alertas;
}