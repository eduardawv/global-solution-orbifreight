package br.com.fiap.orbifreight.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "SENSOR_LEITURA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorLeitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Relacionamento: Várias leituras pertencem a UMA Carga
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carga_id", nullable = false)
    private Carga carga;

    @Column(name = "temperatura", nullable = false)
    private Double temperatura;

    @Column(name = "umidade", nullable = false)
    private Double umidade;

    // Aqui usamos o Embedded! O Hibernate vai criar as colunas "latitude" e "longitude" nesta tabela.
    @Embedded
    private CoordenadaGPS coordenadas;

    @Column(name = "data_hora_leitura", nullable = false)
    private LocalDateTime dataHoraLeitura;
}