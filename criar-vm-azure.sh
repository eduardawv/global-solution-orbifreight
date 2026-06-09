#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight — Criar VM AlmaLinux no Azure
#  Tenta automaticamente vários tamanhos e regioes
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

RESOURCE_GROUP="rg-orbifreight"
VM_NAME="vm-orbifreight-rm564434"
ADMIN_USER="azureuser"
ADMIN_PASSWORD="OrbiFreight.GS.2026"
IMAGE="almalinux:almalinux-x86_64:9-gen2:latest"

# Lista de regioes para tentar (na ordem)
LOCATIONS=("eastus2" "westus2" "centralus" "northeurope" "westeurope")

# Lista de tamanhos para tentar (na ordem, todos com 2+ vCPU / 4+ GB RAM)
SIZES=("Standard_D2s_v5" "Standard_D2s_v3" "Standard_DS2_v2" "Standard_B2ms" "Standard_D2_v3")

echo ""
echo "OrbiFreight — Criando VM AlmaLinux no Azure..."
echo "  VM    : $VM_NAME"
echo "  Imagem: AlmaLinux 9"
echo ""

# Limpar grupo existente se houver
echo "Limpando recursos anteriores (se existirem)..."
az group delete --name "$RESOURCE_GROUP" --yes --no-wait 2>/dev/null || true
sleep 5

SUCCESS=false

for LOCATION in "${LOCATIONS[@]}"; do
  for SIZE in "${SIZES[@]}"; do
    echo ""
    echo "Tentando: regiao=$LOCATION  tamanho=$SIZE"

    # Criar Resource Group
    az group create \
      --name "$RESOURCE_GROUP" \
      --location "$LOCATION" \
      --output none 2>/dev/null || true

    # Tentar criar VM
    if az vm create \
      --resource-group "$RESOURCE_GROUP" \
      --name "$VM_NAME" \
      --image "$IMAGE" \
      --size "$SIZE" \
      --admin-username "$ADMIN_USER" \
      --admin-password "$ADMIN_PASSWORD" \
      --public-ip-sku Standard \
      --output none 2>/dev/null; then

      echo "OK! VM criada com sucesso."
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
  echo "Tente fazer o login novamente com: az login"
  exit 1
fi

# Abrir porta 8080
echo ""
echo "Abrindo porta 8080..."
az vm open-port \
  --resource-group "$RESOURCE_GROUP" \
  --name "$VM_NAME" \
  --port 8080 \
  --priority 1001 \
  --output none

# Pegar IP
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
echo "  API ficara em:"
echo "    http://$IP:8080/swagger-ui.html"
echo ""
echo "  Para DESLIGAR (economizar credito):"
echo "    az vm deallocate -g $RESOURCE_GROUP -n $VM_NAME"
echo "  Para LIGAR de novo:"
echo "    az vm start -g $RESOURCE_GROUP -n $VM_NAME"
echo ""
