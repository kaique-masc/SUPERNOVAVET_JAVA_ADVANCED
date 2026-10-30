#!/bin/bash
# ============================================================
# SUPERNOVA VET - PUBLICACAO DA API NO AZURE
# Arquitetura: ACR + ACI, com o banco Oracle da FIAP
# Executar no Azure Cloud Shell (Bash), na pasta do repositorio
# ============================================================

set -e

RESOURCE_GROUP="rg-supernovavet-api"
LOCATION="brazilsouth"
ACR_NAME="acrsupernovaapi563982"
ACR_SERVER="$ACR_NAME.azurecr.io"
API_CONTAINER="aci-supernovavet-app"
API_DNS="supernovavet-app-563982"
IMAGE_NAME="supernovavet-api"
IMAGE_TAG="v1"

echo "Usuario do Oracle da FIAP (ex.: rm563982):"
read DB_USERNAME
echo "Senha do Oracle da FIAP:"
read -s DB_PASSWORD
echo ""

echo "Criando Resource Group..."
az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --output table

echo "Registrando providers..."
az provider register --namespace Microsoft.ContainerRegistry --wait
az provider register --namespace Microsoft.ContainerInstance --wait

echo "Criando Azure Container Registry..."
if az acr show --name "$ACR_NAME" --resource-group "$RESOURCE_GROUP" > /dev/null 2>&1; then
  echo "ACR $ACR_NAME ja existe, reaproveitando."
else
  az acr create \
    --resource-group "$RESOURCE_GROUP" \
    --name "$ACR_NAME" \
    --sku Basic \
    --admin-enabled true \
    --output table
fi

echo "Gerando a imagem da API no ACR..."
az acr build \
  --registry "$ACR_NAME" \
  --image "$IMAGE_NAME:$IMAGE_TAG" \
  .

ACR_USER=$(az acr credential show --name "$ACR_NAME" --query username -o tsv)
ACR_PASS=$(az acr credential show --name "$ACR_NAME" --query "passwords[0].value" -o tsv)

echo "Criando o container da API..."
az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$API_CONTAINER" \
  --image "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG" \
  --registry-login-server "$ACR_SERVER" \
  --registry-username "$ACR_USER" \
  --registry-password "$ACR_PASS" \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 8080 \
  --ip-address Public \
  --dns-name-label "$API_DNS" \
  --restart-policy Always \
  --secure-environment-variables \
    DB_USERNAME="$DB_USERNAME" \
    DB_PASSWORD="$DB_PASSWORD" \
  --output table

echo ""
echo "API publicada. Aguarde cerca de 1 minuto para ela iniciar."
echo "Swagger: http://$API_DNS.$LOCATION.azurecontainer.io:8080/swagger-ui/index.html"
echo "URL da API: http://$API_DNS.$LOCATION.azurecontainer.io:8080"
echo "Logs: az container logs --resource-group $RESOURCE_GROUP --name $API_CONTAINER"
