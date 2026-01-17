@echo off
REM Script para executar teste de carga K6 - 1000 TPS no Windows

echo ========================================
echo   Teste de Carga K6 - Person API
echo   1000 TPS por 15 minutos
echo   Menor quantidade de VUs
echo ========================================
echo.

REM Verifica se K6 está instalado
where k6 >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERRO] K6 nao encontrado!
    echo.
    echo Instale o K6:
    echo   choco install k6
    echo   ou
    echo   scoop install k6
    echo.
    pause
    exit /b 1
)

echo [OK] K6 encontrado
k6 version
echo.

REM Define URL base (pode ser alterada)
set BASE_URL=http://localhost:8080

echo [INFO] Base URL: %BASE_URL%
echo [INFO] Target: 1000 TPS
echo [INFO] VUs: 50-2000 (otimizado)
echo [INFO] Duração: ~20 minutos
echo [INFO] Iniciando teste...
echo.

REM Executa o teste
k6 run -e BASE_URL=%BASE_URL% person-api-1000-tps.js

echo.
echo ========================================
echo   Teste finalizado!
echo ========================================
pause

