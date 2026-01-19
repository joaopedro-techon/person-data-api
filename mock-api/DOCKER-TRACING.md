# Mock API com OpenTelemetry no Docker

O Mock API está configurado para enviar traces diretamente para o Jaeger (sem OpenTelemetry Collector).

## 🏗️ Como Funciona

1. **OpenTelemetry Agent**: Copiado para dentro do container durante o build
2. **Configuração via ENV**: Variáveis de ambiente configuram o Agent
3. **Envio Direto**: Traces são enviados diretamente para `jaeger:4317` (OTLP gRPC)

## 🚀 Como Buildar e Rodar

### Pré-requisito

Certifique-se de que o arquivo `opentelemetry-javaagent.jar` existe na **raiz do projeto**:

```bash
# Se não existir, baixe:
.\download-otel-agent.bat  # Windows
# ou
./download-otel-agent.sh   # Linux/Mac
```

### Build e Start

```bash
# Build da imagem
docker-compose build mock-api

# Iniciar todos os serviços (incluindo Jaeger)
docker-compose up -d

# Ver logs do mock-api
docker-compose logs -f mock-api
```

## 🔍 Verificar Traces

1. Acesse o Jaeger UI: http://localhost:16686
2. No dropdown "Service", você deve ver:
   - `person-api` (se estiver rodando com Agent)
   - `mock-api` ✅
3. Selecione `mock-api` e clique em "Find Traces"
4. Você verá os traces com os spans explícitos:
   - `mock-api.simulateLatency` [500ms]
   - `mock-api.buildResponse` [<1ms]

## 🐛 Troubleshooting

### Erro: "opentelemetry-javaagent.jar not found"

**Causa**: O arquivo não está na raiz do projeto.

**Solução**:
```bash
# Verificar se existe
ls opentelemetry-javaagent.jar  # Linux/Mac
dir opentelemetry-javaagent.jar  # Windows

# Se não existir, baixar
.\download-otel-agent.bat
```

### Mock API não aparece no Jaeger

**Verificações**:

1. **Jaeger está rodando?**
   ```bash
   docker-compose ps jaeger
   ```

2. **Mock API está na mesma rede?**
   ```bash
   docker-compose ps mock-api
   # Deve mostrar "monitoring-network"
   ```

3. **Verificar logs do Mock API:**
   ```bash
   docker-compose logs mock-api | grep -i otel
   ```

4. **Testar conectividade:**
   ```bash
   # Dentro do container do mock-api
   docker exec -it mock-external-api curl http://jaeger:4317
   ```

### Traces não aparecem

**Causa**: O Jaeger pode não estar recebendo traces.

**Solução**:
1. Verifique se `COLLECTOR_OTLP_ENABLED=true` está configurado no Jaeger
2. Verifique se a porta 4317 está exposta no docker-compose.yml
3. Reinicie o Jaeger:
   ```bash
   docker-compose restart jaeger
   ```

## 📊 Estrutura do Trace

Quando você faz uma requisição através do `person-api`:

```
person-api: GET /api/person/{id}
  └── person-api: HTTP GET http://mock-api:8089/external-person/1
      └── mock-api: GET /external-person/{id}
          ├── mock-api.simulateLatency [500ms]
          └── mock-api.buildResponse [<1ms]
```

## ⚙️ Configuração

As configurações do OpenTelemetry são feitas via variáveis de ambiente no `docker-compose.yml`:

```yaml
environment:
  - OTEL_SERVICE_NAME=mock-api
  - OTEL_TRACES_EXPORTER=otlp
  - OTEL_EXPORTER_OTLP_ENDPOINT=http://jaeger:4317
  - OTEL_EXPORTER_OTLP_PROTOCOL=grpc
  - OTEL_METRICS_EXPORTER=none
  - OTEL_LOGS_EXPORTER=none
  - OTEL_TRACES_SAMPLER=always_on
```

**Nota**: `OTEL_TRACES_SAMPLER=always_on` significa que **todos** os traces são enviados (sem sampling). Se quiser reduzir o volume, você pode mudar para `traceidratio` ou `parentbased_always_on`.

