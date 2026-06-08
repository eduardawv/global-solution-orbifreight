#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight — Criar VM AlmaLinux no Azure (automático)
#  Cria a VM, abre as portas e já mostra o comando de conexão
#
#  COMO USAR:
#    1. Tenha o Azure CLI instalado (ou use o Azure Cloud Shell no portal)
#    2. Rode:  az login
#    3. Rode:  chmod +x criar-vm-azure.sh && ./criar-vm-azure.sh
#
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

set -e

# ── Configurações (pode ajustar se quiser) ─────────────────────────────
RESOURCE_GROUP="rg-orbifreight"
LOCATION="brazilsouth"
VM_NAME="vm-orbifreight-rm564434"
VM_SIZE="Standard_B2s"
ADMIN_USER="azureuser"
ADMIN_PASSWORD="OrbiFreight@2026!"   # ⚠️ troque por uma senha sua se quiser
IMAGE="almalinux:almalinux-x86_64:9-gen2:latest"

echo ""
echo "🛰️  OrbiFreight — Criando VM AlmaLinux no Azure..."
echo "   VM    : $VM_NAME"
echo "   Tipo  : $VM_SIZE (2 vCPU, 4 GB RAM)"
echo "   Região: $LOCATION"
echo "   Imagem: AlmaLinux 9"
echo ""

# ── 1. Aceitar os termos da imagem AlmaLinux ───────────────────────────
echo "📜 [1/5] Aceitando termos da imagem AlmaLinux..."
az vm image terms accept --urn "$IMAGE" --output none || true

# ── 2. Criar o Resource Group ──────────────────────────────────────────
echo "📦 [2/5] Criando Resource Group..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION \
  --output none

# ── 3. Criar a VM ──────────────────────────────────────────────────────
echo "🖥️  [3/5] Criando a VM (pode levar ~2 minutos)..."
az vm create \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --image "$IMAGE" \
  --size $VM_SIZE \
  --admin-username $ADMIN_USER \
  --admin-password "$ADMIN_PASSWORD" \
  --authentication-type password \
  --public-ip-sku Standard \
  --output none

# ── 4. Abrir as portas necessárias ─────────────────────────────────────
echo "🔓 [4/5] Liberando portas 22 (SSH), 80 e 8080 (API)..."
az vm open-port \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --port 8080 \
  --priority 1001 \
  --output none

az vm open-port \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --port 80 \
  --priority 1002 \
  --output none

# ── 5. Pegar o IP público ──────────────────────────────────────────────
echo "🌐 [5/5] Obtendo IP público..."
IP=$(az vm show \
  --resource-group $RESOURCE_GROUP \
  --name $VM_NAME \
  --show-details \
  --query publicIps \
  --output tsv)

echo ""
echo "═══════════════════════════════════════════════════"
echo "✅ VM CRIADA COM SUCESSO!"
echo "═══════════════════════════════════════════════════"
echo ""
echo "  IP Público : $IP"
echo "  Usuário    : $ADMIN_USER"
echo "  Senha      : $ADMIN_PASSWORD"
echo ""
echo "  ▶ Para conectar na VM, rode:"
echo "      ssh $ADMIN_USER@$IP"
echo ""
echo "  ▶ Depois de conectar, dentro da VM:"
echo "      git clone <URL_DO_SEU_REPO>"
echo "      cd global-solution-orbifreight"
echo "      chmod +x vm-setup-almalinux.sh && ./vm-setup-almalinux.sh"
echo "      exit  (e reconecte)"
echo "      docker compose up -d --build"
echo ""
echo "  ▶ A API ficará acessível em:"
echo "      http://$IP:8080/swagger-ui.html"
echo ""
echo "  💡 Para DESLIGAR a VM (economizar crédito):"
echo "      az vm deallocate -g $RESOURCE_GROUP -n $VM_NAME"
echo "  💡 Para LIGAR de novo:"
echo "      az vm start -g $RESOURCE_GROUP -n $VM_NAME"
echo ""
