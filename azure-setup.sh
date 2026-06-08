#!/bin/bash
# ═══════════════════════════════════════════════════════════════════════
#  OrbiFreight — Script de criação de recursos Azure
#  Execute UMA VEZ para criar toda a infraestrutura
#  Pré-requisito: az login já executado
#  RM564434 — Eduarda Weiss Ventura
# ═══════════════════════════════════════════════════════════════════════

set -e  # para se qualquer comando falhar

# ── Variáveis ──────────────────────────────────────────────────────────
RESOURCE_GROUP="rg-orbifreight"
LOCATION="brazilsouth"
ACR_NAME="orbifreightacr$(date +%s | tail -c 5)"  # nome único global
POSTGRES_SERVER="orbifreight-db-rm564434"
CONTAINER_APP_ENV="orbifreight-env"
CONTAINER_APP_NAME="orbifreight-api-rm564434"
DB_NAME="orbifreight"
DB_USER="orbiuser"
DB_PASSWORD="OrbiGS2026!Senha"

echo ""
echo "🛰️  OrbiFreight — Provisionando Azure..."
echo "   Resource Group : $RESOURCE_GROUP"
echo "   Localização    : $LOCATION"
echo "   ACR            : $ACR_NAME"
echo "   PostgreSQL     : $POSTGRES_SERVER"
echo ""

# ── 1. Resource Group ──────────────────────────────────────────────────
echo "📦 [1/6] Criando Resource Group..."
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION \
  --output table

# ── 2. Azure Container Registry ────────────────────────────────────────
echo "🐳 [2/6] Criando Azure Container Registry..."
az acr create \
  --name $ACR_NAME \
  --resource-group $RESOURCE_GROUP \
  --sku Basic \
  --admin-enabled true \
  --output table

ACR_SERVER=$(az acr show --name $ACR_NAME --query loginServer -o tsv)
echo "   ACR Server: $ACR_SERVER"

# ── 3. Build e Push da imagem (AlmaLinux) ──────────────────────────────
echo "🔨 [3/6] Build da imagem Docker (AlmaLinux 9 + Java 21)..."
az acr build \
  --registry $ACR_NAME \
  --image orbifreight-api-rm564434:latest \
  --file Dockerfile \
  .

# ── 4. Azure Database for PostgreSQL Flexible Server ───────────────────
echo "🗄️  [4/6] Criando PostgreSQL Flexible Server..."
az postgres flexible-server create \
  --name $POSTGRES_SERVER \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --admin-user $DB_USER \
  --admin-password "$DB_PASSWORD" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --storage-size 32 \
  --version 15 \
  --public-access 0.0.0.0-255.255.255.255 \
  --output table

az postgres flexible-server db create \
  --server-name $POSTGRES_SERVER \
  --resource-group $RESOURCE_GROUP \
  --database-name $DB_NAME

DB_HOST=$(az postgres flexible-server show \
  --name $POSTGRES_SERVER \
  --resource-group $RESOURCE_GROUP \
  --query fullyQualifiedDomainName -o tsv)

echo "   DB Host: $DB_HOST"

# ── 5. Azure Container Apps Environment ────────────────────────────────
echo "☁️  [5/6] Criando Container Apps Environment..."
az containerapp env create \
  --name $CONTAINER_APP_ENV \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --output table

# ── 6. Deploy da API Java ───────────────────────────────────────────────
echo "🚀 [6/6] Fazendo deploy da API Java..."
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv)

az containerapp create \
  --name $CONTAINER_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --environment $CONTAINER_APP_ENV \
  --image $ACR_SERVER/orbifreight-api-rm564434:latest \
  --registry-server $ACR_SERVER \
  --registry-username $ACR_USERNAME \
  --registry-password "$ACR_PASSWORD" \
  --target-port 8080 \
  --ingress external \
  --min-replicas 1 \
  --max-replicas 2 \
  --cpu 0.5 \
  --memory 1.0Gi \
  --env-vars \
    SPRING_PROFILES_ACTIVE=docker \
    DB_HOST="$DB_HOST" \
    DB_PORT=5432 \
    DB_NAME="$DB_NAME" \
    DB_USERNAME="$DB_USER" \
    DB_PASSWORD="$DB_PASSWORD" \
    JWT_SECRET="orbifreight-jwt-secret-gssolution-2026" \
    APP_PORT=8080 \
  --output table

# ── Resultado ─────────────────────────────────────────────────────────
API_URL=$(az containerapp show \
  --name $CONTAINER_APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --query 'properties.configuration.ingress.fqdn' -o tsv)

echo ""
echo "═══════════════════════════════════════════"
echo "✅ DEPLOY CONCLUÍDO!"
echo "═══════════════════════════════════════════"
echo ""
echo "  API URL    : https://$API_URL"
echo "  Swagger    : https://$API_URL/swagger-ui.html"
echo "  ACR        : $ACR_SERVER"
echo "  PostgreSQL : $DB_HOST"
echo ""
echo "  Salve estes valores para o azure-pipelines.yml:"
echo "  ACR_NAME=$ACR_NAME"
echo ""
