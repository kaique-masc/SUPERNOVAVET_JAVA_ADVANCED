#!/bin/bash
# Remove todos os recursos criados pelo publicar-api.sh

az group delete --name "rg-supernovavet-api" --yes --no-wait
echo "Remocao iniciada: rg-supernovavet-api"
