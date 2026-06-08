package br.com.fiap.orbifreight.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TIPO_CARGA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoCarga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "temp_min", nullable = false)
    private Double tempMin;

    @Column(name = "temp_max", nullable = false)
    private Double tempMax;

    @Column(name = "umidade_max", nullable = false)
    private Double umidadeMax;

    @Column(name = "prazo_max_horas", nullable = false)
    private Integer prazoMaxHoras;
}