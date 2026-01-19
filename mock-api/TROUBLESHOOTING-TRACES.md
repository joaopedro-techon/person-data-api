# Troubleshooting: Mock API não aparece no Trace

Se o `mock-api` não está aparecendo no Jaeger, siga estes passos:

## ✅ Checklist de Verificação

### 1. Verificar se o Mock API está rodando com OpenTelemetry Agent

**Sintoma**: No Jaeger, você vê apenas `person-api`, mas não `mock-api`.

**Solução**: Certifique-se de que o Mock API está rodando com o script correto:

```bash
cd mock-api
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

**Verificação**: Ao iniciar, você deve ver no console:
```
========================================
Starting Mock API with OpenTelemetry
========================================
Using OpenTelemetry Java Agent: ..\opentelemetry-javaagent.jar
Service Name: mock-api
Collector Endpoint: http://localhost:4317
```

### 2. Verificar se o OpenTelemetry Collector está rodando

```bash
docker-compose ps otel-collector
```

Deve mostrar o container `otel-collector` como `Up`.

### 3. Verificar se o Mock API está recebendo requisições

Teste diretamente o Mock API:

```bash
curl http://localhost:8089/external-person/1
```

Deve retornar uma resposta JSON.

### 4. Verificar logs do OpenTelemetry Collector

```bash
docker-compose logs -f otel-collector
```

Você deve ver logs indicando que traces estão sendo recebidos:
```
TracesExporter  {"kind": "exporter", "name": "otlp", "traces": [...]}
```

### 5. Verificar se o Person API está propagando contexto

O OpenTelemetry Agent **automaticamente** propaga o contexto via headers HTTP (`traceparent`, `tracestate`). Não é necessário fazer nada manualmente.

### 6. Verificar se os spans explícitos estão sendo criados

Os spans explícitos (`mock-api.simulateLatency`, `mock-api.buildResponse`) aparecerão como **filhos** do span HTTP criado automaticamente pelo Agent.

## 🔍 Como Verificar no Jaeger

1. Acesse: http://localhost:16686
2. No dropdown "Service", você deve ver:
   - `person-api` ✅
   - `mock-api` ✅ (se estiver rodando com Agent)
3. Selecione `person-api` e clique em "Find Traces"
4. Clique em um trace específico
5. Você deve ver:
   ```
   person-api: GET /api/person/{id}
     └── person-api: HTTP GET http://localhost:8089/external-person/1
         └── mock-api: GET /external-person/{id}
             ├── mock-api.simulateLatency [500ms]
             └── mock-api.buildResponse [<1ms]
   ```

## 🐛 Problemas Comuns

### Problema 1: Mock API não aparece no dropdown de serviços

**Causa**: Mock API não está rodando com OpenTelemetry Agent.

**Solução**: 
- Pare o Mock API (Ctrl+C)
- Execute novamente com `.\run-with-otel.bat`
- Verifique se o arquivo `opentelemetry-javaagent.jar` existe na raiz do projeto

### Problema 2: Mock API aparece, mas não há spans filhos

**Causa**: Os spans explícitos não estão sendo criados corretamente.

**Solução**: 
- Verifique se o código foi compilado: `mvn clean package`
- Verifique se o Mock API foi reiniciado após a compilação

### Problema 3: Trace aparece, mas sem detalhes do Mock API

**Causa**: Sampling pode estar filtrando traces.

**Solução**: 
- Verifique o `otel-collector-config.yaml`
- O threshold atual é 100ms (traces com latência > 100ms são enviados)
- Como o Mock API tem 500ms de latência, todos os traces devem ser enviados

### Problema 4: Erro "Connection refused" no Mock API

**Causa**: OpenTelemetry Collector não está acessível.

**Solução**:
```bash
# Verificar se o Collector está rodando
docker-compose ps otel-collector

# Se não estiver, iniciar
docker-compose up -d otel-collector

# Verificar logs
docker-compose logs otel-collector
```

## 📊 Teste Completo

Execute este teste completo:

```bash
# Terminal 1: Iniciar Collector e Jaeger
docker-compose up -d otel-collector jaeger

# Terminal 2: Iniciar Mock API com Agent
cd mock-api
.\run-with-otel.bat

# Terminal 3: Iniciar Person API com Agent
cd ..
.\run-with-otel.bat

# Terminal 4: Fazer requisição
curl http://localhost:8080/api/person/1

# Verificar no Jaeger
# Acesse: http://localhost:16686
# Selecione: person-api
# Clique: Find Traces
# Clique no trace mais recente
```

Você deve ver o trace completo com ambos os serviços!

