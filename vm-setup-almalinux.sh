#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight — Instalação do Docker na VM AlmaLinux (Azure)
#  Execute ESTE script DENTRO da VM, uma única vez
#  Uso:  chmod +x vm-setup-almalinux.sh && ./vm-setup-almalinux.sh
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

set -e

echo ""
echo "🛰️  OrbiFreight — Configurando VM AlmaLinux..."
echo ""

# ── 1. Atualizar o sistema ──────────────────────────────────────────────
echo "📦 [1/6] Atualizando pacotes do sistema..."
sudo dnf update -y

# ── 2. Instalar utilitários ─────────────────────────────────────────────
echo "🔧 [2/6] Instalando git e utilitários..."
sudo dnf install -y git dnf-plugins-core

# ── 3. Adicionar repositório oficial do Docker ──────────────────────────
echo "🐳 [3/6] Adicionando repositório do Docker..."
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# ── 4. Instalar Docker Engine + Docker Compose ──────────────────────────
echo "⚙️  [4/6] Instalando Docker Engine e Docker Compose..."
sudo dnf install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# ── 5. Iniciar e habilitar o Docker ─────────────────────────────────────
echo "🚀 [5/6] Iniciando o serviço Docker..."
sudo systemctl start docker
sudo systemctl enable docker

# ── 6. Permitir rodar docker sem sudo ───────────────────────────────────
echo "👤 [6/6] Adicionando seu usuário ao grupo docker..."
sudo usermod -aG docker $USER

echo ""
echo "═══════════════════════════════════════════"
echo "✅ DOCKER INSTALADO COM SUCESSO!"
echo "═══════════════════════════════════════════"
echo ""
docker --version
docker compose version
echo ""
echo "⚠️  IMPORTANTE: saia da VM (digite 'exit') e conecte de novo via SSH"
echo "   para que o 'docker sem sudo' funcione."
echo ""
echo "Depois rode:  cd global-solution-orbifreight && docker compose up -d --build"
echo ""
