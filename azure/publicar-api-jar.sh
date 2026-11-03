#!/bin/bash
# ============================================================
# SUPERNOVA VET - PUBLICACAO DA API NO AZURE (SEM ACR)
# Para assinaturas que nao permitem ACR Tasks (az acr build).
# Gera o .jar no Cloud Shell, guarda em um Azure Files e roda
# o container com a imagem OpenJDK 17 da Microsoft.
# Executar no Azure Cloud Shell (Bash), na pasta do repositorio
# ============================================================

set -e

RESOURCE_GROUP="rg-supernovavet-api"
LOCATION="brazilsouth"
STORAGE_ACCOUNT="stsupernova565802"
SHARE_NAME="app"
API_CONTAINER="aci-supernovavet-app"
API_DNS="supernovavet-app-565802"
IMAGE="mcr.microsoft.com/openjdk/jdk:17-ubuntu"

echo "Usuario do Oracle da FIAP (ex.: rm563982):"
read DB_USERNAME
echo "Senha do Oracle da FIAP:"
read -s DB_PASSWORD
echo ""
echo "Chave da API Gemini:"
read -rs GEMINI_API_KEY
echo ""


echo "Gerando o .jar da API..."
chmod +x mvnw
./mvnw -q -DskipTests package

echo "Criando Resource Group..."
az group create --name "$RESOURCE_GROUP" --location "$LOCATION" --output table

echo "Registrando providers..."
az provider register --namespace Microsoft.Storage --wait
az provider register --namespace Microsoft.ContainerInstance --wait

echo "Criando o armazenamento do .jar..."
az storage account create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$STORAGE_ACCOUNT" \
  --location "$LOCATION" \
  --sku Standard_LRS \
  --output table

STORAGE_KEY=$(az storage account keys list --resource-group "$RESOURCE_GROUP" --account-name "$STORAGE_ACCOUNT" --query "[0].value" -o tsv)

az storage share create --account-name "$STORAGE_ACCOUNT" --account-key "$STORAGE_KEY" --name "$SHARE_NAME" --output table

az storage file upload \
  --account-name "$STORAGE_ACCOUNT" \
  --account-key "$STORAGE_KEY" \
  --share-name "$SHARE_NAME" \
  --source target/Novamonitor-0.0.1-SNAPSHOT.jar \
  --path app.jar

echo "Criando o container da API..."
az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$API_CONTAINER" \
  --image "$IMAGE" \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 8080 \
  --ip-address Public \
  --dns-name-label "$API_DNS" \
  --restart-policy Always \
  --azure-file-volume-account-name "$STORAGE_ACCOUNT" \
  --azure-file-volume-account-key "$STORAGE_KEY" \
  --azure-file-volume-share-name "$SHARE_NAME" \
  --azure-file-volume-mount-path /app \
  --command-line "java -jar /app/app.jar" \
  --secure-environment-variables \
    DB_USERNAME="$DB_USERNAME" \
    DB_PASSWORD="$DB_PASSWORD" \
    GEMINI_API_KEY="$GEMINI_API_KEY" \
  --output table

echo ""
echo "API publicada. Aguarde cerca de 1 minuto para ela iniciar."
echo "Swagger: http://$API_DNS.$LOCATION.azurecontainer.io:8080/swagger-ui/index.html"
echo "URL da API: http://$API_DNS.$LOCATION.azurecontainer.io:8080"
echo "Logs: az container logs --resource-group $RESOURCE_GROUP --name $API_CONTAINER"
