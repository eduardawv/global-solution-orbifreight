#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight — Criar VM AlmaLinux no Azure
#  Tenta automaticamente varias regioes e tamanhos ate conseguir
#
#  COMO USAR:
#    1. Use o Azure Cloud Shell (portal.azure.com, icone >_) ou Azure CLI
#    2. Faca upload deste arquivo
#    3. Rode:  chmod +x criar-vm-azure.sh && ./criar-vm-azure.sh
#
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

RESOURCE_GROUP="rg-orbifreight"
VM_NAME="vm-orbifreight-rm564434"
ADMIN_USER="azureuser"
ADMIN_PASSWORD="OrbiFreight.GS.2026"
IMAGE="almalinux:almalinux-x86_64:9-gen2:latest"

# Regioes para tentar (na ordem) - contornam o bloqueio de regiao da conta estudante
LOCATIONS=("eastus2" "westus2" "centralus" "northeurope" "westeurope" "canadacentral" "uksouth" "australiaeast")

# Tamanhos para tentar (na ordem) - contornam falta de estoque
SIZES=("Standard_D2s_v5" "Standard_D2s_v3" "Standard_DS2_v2" "Standard_B2ms" "Standard_D2_v3")

echo ""
echo "OrbiFreight — Criando VM AlmaLinux no Azure..."
echo "  VM    : $VM_NAME"
echo "  Imagem: AlmaLinux 9"
echo ""

echo "Limpando recursos anteriores (se existirem)..."
az group delete --name "$RESOURCE_GROUP" --yes --no-wait 2>/dev/null || true
sleep 8

SUCCESS=false

for LOCATION in "${LOCATIONS[@]}"; do
  for SIZE in "${SIZES[@]}"; do
    echo ""
    echo "Tentando: regiao=$LOCATION  tamanho=$SIZE"

    az group create \
      --name "$RESOURCE_GROUP" \
      --location "$LOCATION" \
      --output none 2>/dev/null || true

    if az vm create \
      --resource-group "$RESOURCE_GROUP" \
      --name "$VM_NAME" \
      --image "$IMAGE" \
      --size "$SIZE" \
      --admin-username "$ADMIN_USER" \
      --admin-password "$ADMIN_PASSWORD" \
      --public-ip-sku Standard \
      --output none 2>/dev/null; then

      echo "OK! VM criada."
      SUCCESS=true
      FINAL_LOCATION=$LOCATION
      FINAL_SIZE=$SIZE
      break 2
    else
      echo "Falhou. Tentando proximo..."
      az group delete --name "$RESOURCE_GROUP" --yes --no-wait 2>/dev/null || true
      sleep 8
    fi
  done
done

if [ "$SUCCESS" = false ]; then
  echo ""
  echo "ERRO: Nao foi possivel criar a VM em nenhuma regiao/tamanho."
  echo "Rode 'az login' de novo e tente novamente."
  exit 1
fi

echo ""
echo "Abrindo porta 8080 (API)..."
az vm open-port \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --port 8080 \
  --priority 1001 \
  --output none

IP=$(az vm show \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --show-details \
  --query publicIps \
  --output tsv)

echo ""
echo "============================================"
echo "  VM CRIADA COM SUCESSO!"
echo "============================================"
echo ""
echo "  IP publico : $IP"
echo "  Regiao     : $FINAL_LOCATION"
echo "  Tamanho    : $FINAL_SIZE"
echo "  Usuario    : $ADMIN_USER"
echo "  Senha      : $ADMIN_PASSWORD"
echo ""
echo "  Conecte na VM:"
echo "    ssh $ADMIN_USER@$IP"
echo ""
echo "  Dentro da VM:"
echo "    git clone https://github.com/eduardawv/global-solution-orbifreight.git"
echo "    cd global-solution-orbifreight"
echo "    chmod +x vm-setup-almalinux.sh && ./vm-setup-almalinux.sh"
echo "    exit   (e reconecte por SSH)"
echo "    cd global-solution-orbifreight"
echo "    cp .env.example .env && docker compose up -d --build"
echo ""
echo "  API ficara em:"
echo "    http://$IP:8080/swagger-ui.html"
echo ""
echo "  Para DESLIGAR (economizar credito):"
echo "    az vm deallocate -g $RESOURCE_GROUP -n $VM_NAME"
echo "  Para LIGAR de novo:"
echo "    az vm start -g $RESOURCE_GROUP -n $VM_NAME"
echo "  Para DELETAR TUDO:"
echo "    az group delete --name $RESOURCE_GROUP --yes --no-wait"
echo ""
