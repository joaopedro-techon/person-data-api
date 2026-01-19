#!/bin/bash
# Script para rodar o Mock API com OpenTelemetry Java Agent
# Instrumentação automática para tracing distribuído

set -e

echo "========================================"
echo "Starting Mock API with OpenTelemetry"
echo "========================================"
echo ""

# Verifica se o agent está na raiz do projeto principal
AGENT_FILE="../opentelemetry-javaagent.jar"

if [ ! -f "$AGENT_FILE" ]; then
    echo "ERROR: OpenTelemetry Java Agent not found!"
    echo "Please run download-otel-agent.sh from the root directory first."
    echo ""
    exit 1
fi

echo "Using OpenTelemetry Java Agent: $AGENT_FILE"
echo "Service Name: mock-api"
echo "Collector Endpoint: http://localhost:4317"
echo ""
echo "Starting Mock API..."
echo ""

# Configurações do OpenTelemetry
export OTEL_SERVICE_NAME=mock-api
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317
export OTEL_EXPORTER_OTLP_PROTOCOL=grpc
export OTEL_METRICS_EXPORTER=none
export OTEL_LOGS_EXPORTER=none

# Executa a aplicação com o agent
java \
  -javaagent:$AGENT_FILE \
  -Dotel.service.name=$OTEL_SERVICE_NAME \
  -Dotel.traces.exporter=$OTEL_TRACES_EXPORTER \
  -Dotel.exporter.otlp.endpoint=$OTEL_EXPORTER_OTLP_ENDPOINT \
  -Dotel.exporter.otlp.protocol=$OTEL_EXPORTER_OTLP_PROTOCOL \
  -Dotel.metrics.exporter=$OTEL_METRICS_EXPORTER \
  -Dotel.logs.exporter=$OTEL_LOGS_EXPORTER \
  -jar target/mock-external-api-0.0.1-SNAPSHOT.jar

