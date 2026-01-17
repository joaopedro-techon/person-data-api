@echo off
REM Script para baixar o OpenTelemetry Java Agent
REM Baixa a versão mais recente do agent

echo ========================================
echo Downloading OpenTelemetry Java Agent
echo ========================================
echo.

set AGENT_VERSION=2.4.0
set AGENT_URL=https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v%AGENT_VERSION%/opentelemetry-javaagent.jar
set AGENT_FILE=opentelemetry-javaagent.jar

if exist "%AGENT_FILE%" (
    echo Agent already exists: %AGENT_FILE%
    echo Skipping download...
    goto :end
)

echo Downloading version %AGENT_VERSION%...
echo URL: %AGENT_URL%
echo.

curl -LO %AGENT_URL%

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to download OpenTelemetry Java Agent!
    echo Please check your internet connection and try again.
    echo.
    echo You can also download manually from:
    echo https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases
    pause
    exit /b 1
)

if not exist "%AGENT_FILE%" (
    echo.
    echo ERROR: Agent file not found after download!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Download completed successfully!
echo Agent file: %AGENT_FILE%
echo ========================================
echo.

:end
pause

