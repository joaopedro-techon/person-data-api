#!/bin/bash
# Script para rodar a aplicação com OpenTelemetry Java Agent
# Instrumentação automática para Jaeger

set -e

AGENT_FILE="opentelemetry-javaagent.jar"

echo "========================================"
echo "Starting Person API with OpenTelemetry"
echo "========================================"
echo ""

if [ ! -f "$AGENT_FILE" ]; then
    echo "ERROR: OpenTelemetry Java Agent not found!"
    echo "Please run download-otel-agent.sh first."
    echo ""
    exit 1
fi

echo "Using OpenTelemetry Java Agent: $AGENT_FILE"
echo "Service Name: person-api"
echo "Collector Endpoint: http://localhost:4318 (HTTP)"
echo "Sampling: Tail-based (latency > 300ms or errors)"
echo ""
echo "Starting application..."
echo ""

# Configurações do OpenTelemetry
export OTEL_SERVICE_NAME=person-api
export OTEL_TRACES_EXPORTER=otlp
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
export OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
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
  -jar target/person-api-0.0.1-SNAPSHOT.jar

