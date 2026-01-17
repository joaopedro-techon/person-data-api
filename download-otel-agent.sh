#!/bin/bash
# Script para baixar o OpenTelemetry Java Agent
# Baixa a versão mais recente do agent

set -e

AGENT_VERSION="2.4.0"
AGENT_URL="https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${AGENT_VERSION}/opentelemetry-javaagent.jar"
AGENT_FILE="opentelemetry-javaagent.jar"

echo "========================================"
echo "Downloading OpenTelemetry Java Agent"
echo "========================================"
echo ""

if [ -f "$AGENT_FILE" ]; then
    echo "Agent already exists: $AGENT_FILE"
    echo "Skipping download..."
    exit 0
fi

echo "Downloading version $AGENT_VERSION..."
echo "URL: $AGENT_URL"
echo ""

curl -LO "$AGENT_URL"

if [ ! -f "$AGENT_FILE" ]; then
    echo ""
    echo "ERROR: Agent file not found after download!"
    echo ""
    echo "You can also download manually from:"
    echo "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases"
    exit 1
fi

echo ""
echo "========================================"
echo "Download completed successfully!"
echo "Agent file: $AGENT_FILE"
echo "========================================"
echo ""

