package br.com.fiap.orbifreight.controllers;

import br.com.fiap.orbifreight.dtos.LeituraIoTRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/iot")
public class IoTController {

    @PostMapping("/leituras")
    public ResponseEntity<String> receberLeitura(@RequestBody LeituraIoTRequest payload) {

        System.out.println("🌡ALERTA IOT RECEBIDO!");
        System.out.println("Carga: " + payload.carga_id());
        System.out.println("Temperatura: " + payload.temperatura() + "°C");
        System.out.println("Risco: " + payload.risco());
        System.out.println("-------------------------");

        return ResponseEntity.ok("Dados recebidos com sucesso pelo Java!");
    }
}