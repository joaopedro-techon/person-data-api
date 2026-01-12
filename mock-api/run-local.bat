@echo off
REM Script para rodar a API mock localmente no Windows sem Docker
REM Evita problemas de proxy/rede do Docker

echo Starting Mock API on port 8089...
echo (This avoids Docker proxy/network issues)
echo.

cd /d "%~dp0"
call mvn spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error starting Mock API!
    echo Make sure Maven is installed and configured correctly.
    pause
    exit /b 1
)

