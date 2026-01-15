#!/bin/bash
# Script para executar teste de carga K6 no Linux/Mac

echo "========================================"
echo "  Teste de Carga K6 - Person API"
echo "  6000 TPS por 30 minutos"
echo "========================================"
echo ""

# Verifica se K6 está instalado
if ! command -v k6 &> /dev/null; then
    echo "[ERRO] K6 não encontrado!"
    echo ""
    echo "Instale o K6:"
    echo "  Linux: sudo apt-get install k6"
    echo "  macOS: brew install k6"
    echo ""
    exit 1
fi

echo "[OK] K6 encontrado"
k6 version
echo ""

# Define URL base (pode ser alterada)
BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "[INFO] Base URL: $BASE_URL"
echo "[INFO] Iniciando teste..."
echo ""

# Executa o teste
k6 run -e BASE_URL="$BASE_URL" person-api-6000-tps.js

echo ""
echo "========================================"
echo "  Teste finalizado!"
echo "========================================"

