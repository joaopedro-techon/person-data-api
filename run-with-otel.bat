@echo off
REM Script para rodar a aplicação com OpenTelemetry Java Agent
REM Instrumentação automática para Jaeger

echo ========================================
echo Starting Person API with OpenTelemetry
echo ========================================
echo.

set AGENT_FILE=opentelemetry-javaagent.jar

if not exist "%AGENT_FILE%" (
    echo ERROR: OpenTelemetry Java Agent not found!
    echo Please run download-otel-agent.bat first.
    echo.
    pause
    exit /b 1
)

echo Using OpenTelemetry Java Agent: %AGENT_FILE%
echo Service Name: person-api
echo Collector Endpoint: http://localhost:4318 (HTTP) ou http://localhost:4317 (gRPC)
echo Sampling: Tail-based (latency > 300ms or errors)
echo.
echo Starting application...
echo.

REM Configurações do OpenTelemetry
set OTEL_SERVICE_NAME=person-api
set OTEL_TRACES_EXPORTER=otlp
set OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4318
set OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
set OTEL_METRICS_EXPORTER=none
set OTEL_LOGS_EXPORTER=none

REM Executa a aplicação com o agent
java ^
  -javaagent:%AGENT_FILE% ^
  -Dotel.service.name=%OTEL_SERVICE_NAME% ^
  -Dotel.traces.exporter=%OTEL_TRACES_EXPORTER% ^
  -Dotel.exporter.otlp.endpoint=%OTEL_EXPORTER_OTLP_ENDPOINT% ^
  -Dotel.exporter.otlp.protocol=%OTEL_EXPORTER_OTLP_PROTOCOL% ^
  -Dotel.metrics.exporter=%OTEL_METRICS_EXPORTER% ^
  -Dotel.logs.exporter=%OTEL_LOGS_EXPORTER% ^
  -jar target\person-api-0.0.1-SNAPSHOT.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start application!
    pause
    exit /b 1
)

