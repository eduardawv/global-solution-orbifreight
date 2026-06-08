# 🛰️ OrbiFreight API — DevOps com VM AlmaLinux (Azure)

> Monitoramento inteligente de transporte alimentício via IoT + Satélite + IA
> **Stack**: Spring Boot 4 · Java 21 · AlmaLinux 9 · PostgreSQL 15 · Docker · VM Azure

**Global Solution 2026/1 · FIAP · 2TDS**

| Integrante | RM |
|---|---|
| **Eduarda Weiss Ventura** | **564434** |
| Maria Gabriela Landim Severo | 565146 |
| Samara Porto Souza | 559072 |
| Lucas Nunes Soares | 566503 |
| Camily Vitoria Pereira Maciel | 566520 |

---

## 🏗️ Arquitetura Macro

```
┌──────────────────────────────────────────────────────────────┐
│            AZURE — Máquina Virtual (AlmaLinux 9)              │
│                  IP Público + Porta 8080                     │
│                                                              │
│   docker compose up -d                                       │
│   ┌────────────────────────────┐  ┌──────────────────────┐  │
│   │ orbifreight-api-rm564434   │  │ postgres-rm564434     │  │
│   │ Container AlmaLinux 9      │◄─┤ Container PostgreSQL  │  │
│   │ Spring Boot · Java 21      │  │ Volume: pgdata        │  │
│   │ Porta 8080                 │  │ Porta 5432            │  │
│   │ Usuário: orbiuser          │  │                       │  │
│   └────────────────────────────┘  └──────────────────────┘  │
│            Rede bridge: orbifreight-net                      │
└──────────────────────────────────────────────────────────────┘
        ▲ HTTPS/HTTP + JWT
┌───────┴────────┐   ┌─────────────────────┐
│ React Native   │   │ ESP32 IoT (DHT22)   │
│ Mobile App     │   │ POST /iot/leitura    │
└────────────────┘   └─────────────────────┘
```

**Fluxo CI/CD**: GitHub → git clone na VM → docker compose up → IP público

---

## 📋 Pré-requisitos

- Conta Azure (conta estudante FIAP com crédito gratuito)
- Um cliente SSH (o Windows 10/11 já tem no terminal)
- Git instalado no projeto (já vem na VM via script)

> **Você NÃO precisa instalar Docker no seu PC.** Tudo roda na VM.

---

## 🚀 How-to — Do clone à execução em nuvem

### ETAPA 1 — Criar a VM AlmaLinux no Azure

1. Acesse [portal.azure.com](https://portal.azure.com)
2. Pesquise **"Virtual Machines"** → **Create** → **Azure virtual machine**
3. Configure:
   - **Resource group**: criar novo → `rg-orbifreight`
   - **Virtual machine name**: `vm-orbifreight-rm564434`
   - **Region**: Brazil South
   - **Image**: AlmaLinux OS 9 (procure "AlmaLinux" no marketplace)
   - **Size**: `Standard_B2s` (2 vCPU, 4 GB RAM — necessário para Docker)
   - **Authentication type**: Password (mais simples) ou SSH key
   - **Username**: `azureuser`
   - **Password**: crie uma senha forte e anote
4. Na aba **Networking**, em "Inbound ports", marque: **SSH (22)** e **HTTP (80)**
5. Clique em **Review + Create** → **Create**
6. Aguarde ~2 min. Quando pronto, vá para o recurso e **anote o "Public IP address"**

### ETAPA 2 — Liberar a porta 8080 no firewall do Azure

1. Na página da VM → menu lateral → **Networking** → **Network settings**
2. Clique em **Create port rule** → **Inbound port rule**
3. Configure:
   - **Destination port ranges**: `8080`
   - **Protocol**: TCP
   - **Name**: `Porta-API-8080`
4. Clique em **Add**

### ETAPA 3 — Conectar na VM via SSH

No terminal do seu PC (CMD ou PowerShell no Windows):

```bash
ssh azureuser@SEU_IP_PUBLICO
```

Digite a senha que criou. Na primeira vez, digite `yes` para confiar no host.

### ETAPA 4 — Instalar Docker na VM

Já dentro da VM, primeiro envie o projeto. A forma mais fácil é via git:

```bash
git clone https://github.com/SEU_USUARIO/global-solution-orbifreight.git
cd global-solution-orbifreight
```

Rode o script de instalação do Docker:

```bash
chmod +x vm-setup-almalinux.sh
./vm-setup-almalinux.sh
```

Quando terminar, **saia e reconecte** (para o Docker funcionar sem sudo):

```bash
exit
ssh azureuser@SEU_IP_PUBLICO
cd global-solution-orbifreight
```

### ETAPA 5 — Subir os containers

```bash
cp .env.example .env
docker compose up -d --build
```

Aguarde 2-4 minutos na primeira vez (Maven compila + monta imagem AlmaLinux).

### ETAPA 6 — Verificar

```bash
docker ps
```

Você verá os dois containers `Up`. Agora, **do seu PC**, abra no navegador:

```
http://SEU_IP_PUBLICO:8080/swagger-ui.html
```

✅ Se abrir o Swagger pela internet, está rodando EM NUVEM (não localhost)!

---

## 🔍 Evidências Obrigatórias (rode dentro da VM)

### Logs dos containers
```bash
docker logs orbifreight-api-rm564434
docker logs postgres-rm564434
```

### Acesso ao container Java — whoami, pwd, ls
```bash
docker exec -it orbifreight-api-rm564434 bash
whoami    # orbiuser
pwd       # /app
ls -l     # app.jar
exit
```

### Acesso ao container banco
```bash
docker exec -it postgres-rm564434 bash
whoami
pwd
ls -l /var/lib/postgresql/data
exit
```

### SELECT direto no banco (evidência de persistência)
```bash
docker exec -it postgres-rm564434 psql -U orbiuser -d orbifreight -c "\dt"
docker exec -it postgres-rm564434 psql -U orbiuser -d orbifreight -c "SELECT * FROM tipo_carga;"
docker exec -it postgres-rm564434 psql -U orbiuser -d orbifreight -c "SELECT id, placa_veiculo, origem, destino, status FROM carga;"
docker exec -it postgres-rm564434 psql -U orbiuser -d orbifreight -c "SELECT id, carga_id, titulo, nivel, status FROM alerta;"
docker exec -it postgres-rm564434 psql -U orbiuser -d orbifreight -c "SELECT c.placa_veiculo, tc.nome AS tipo, a.titulo, a.nivel FROM carga c JOIN tipo_carga tc ON tc.id=c.tipo_id JOIN alerta a ON a.carga_id=c.id;"
```

---

## 🧪 CRUD da API (via Swagger ou curl)

Acesse `http://SEU_IP_PUBLICO:8080/swagger-ui.html` e execute na ordem:

1. **POST /auth/register** → `{"nome":"Eduarda","email":"eduarda@orbi.com","senha":"senha123","cargo":"GESTOR"}`
2. **POST /auth/login** → copie o token
3. Clique em **Authorize** 🔓 e cole o token
4. **POST /tipos-carga** → `{"nome":"Carne Bovina","tempMin":0,"tempMax":4,"umidadeMax":90,"prazoMaxHoras":48}`
5. **POST /cargas** → `{"tipoId":1,"veiculoId":1,"motoristaId":1,"placaVeiculo":"ABC1D234","origem":"Sao Paulo SP","destino":"Campinas SP","tempMin":0,"tempMax":4,"umidadeMax":90,"status":"ATIVA"}`
6. **POST /alertas** → `{"cargaId":1,"titulo":"Temperatura critica","descricao":"Sensor IoT 6.5C","nivel":"CRITICO","status":"ABERTO"}`
7. **GET /cargas** (Read) · **PUT /cargas/1** (Update) · **DELETE /cargas/1** (Delete)

---

## 🛑 Comandos úteis

```bash
docker compose down          # para os containers
docker compose up -d         # sobe de novo (sem rebuild)
docker compose logs -f       # acompanha logs em tempo real
docker stats                 # uso de CPU/memória
```

---

## ✅ Checklist de Requisitos

| Requisito | Status |
|---|---|
| Dockerfile AlmaLinux 9 personalizado | ✅ |
| Usuário não-root (orbiuser) | ✅ |
| WORKDIR /app | ✅ |
| Variável de ambiente App | ✅ |
| Porta 8080 exposta | ✅ |
| Nome container App com RM564434 | ✅ |
| CRUD completo 2+ tabelas relacionadas | ✅ |
| Volume nomeado (orbifreight-pgdata) | ✅ |
| Variável de ambiente banco | ✅ |
| Porta 5432 exposta | ✅ |
| Nome container banco com RM564434 | ✅ |
| Mesma rede bridge | ✅ |
| Execução background (-d) | ✅ |
| Solução em NUVEM (VM Azure + IP público) | ✅ |

---

*OrbiFreight · Global Solution 2026/1 · FIAP 2TDS · VM AlmaLinux 9*
