#!/bin/bash
# Script para rodar a API mock localmente sem Docker
# Evita problemas de proxy/rede do Docker

echo "Starting Mock API on port 8089..."
echo "(This avoids Docker proxy/network issues)"
echo ""

cd "$(dirname "$0")"
mvn spring-boot:run

if [ $? -ne 0 ]; then
    echo ""
    echo "Error starting Mock API!"
    echo "Make sure Maven is installed and configured correctly."
    exit 1
fi

