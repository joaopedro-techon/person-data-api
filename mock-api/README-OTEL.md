# Mock API com OpenTelemetry - Tracing Distribuído

O Mock API está configurado para ser instrumentado com OpenTelemetry Java Agent, permitindo ver traces de ponta a ponta junto com a aplicação principal.

## Como Executar com Tracing

### Pré-requisitos

1. **Baixar o OpenTelemetry Java Agent** (se ainda não tiver):
   ```bash
   # Na raiz do projeto principal
   cd ..
   .\download-otel-agent.bat  # Windows
   # ou
   ./download-otel-agent.sh   # Linux/Mac
   ```

2. **Compilar o Mock API**:
   ```bash
   cd mock-api
   mvn clean package
   ```

### Executar com OpenTelemetry

**Windows:**
```bash
cd mock-api
.\run-with-otel.bat
```

**Linux/Mac:**
```bash
cd mock-api
chmod +x run-with-otel.sh
./run-with-otel.sh
```

## Configuração

O Mock API está configurado para:
- **Service Name**: `mock-api`
- **Collector Endpoint**: `http://localhost:4317` (OTLP gRPC)
- **Mesmo Collector**: Usa o mesmo OpenTelemetry Collector da aplicação principal

## Traces de Ponta a Ponta

Quando ambas as aplicações estão instrumentadas:

```
Requisição HTTP
    ↓
person-api (porta 8080)
    ├── PersonController.getPerson()
    │   └── PersonService.getPersonById()
    │       └── ExternalPersonClient.getExternalPerson() [Feign]
    │           ↓ HTTP Request
    │           └── mock-api (porta 8089)
    │               └── MockPersonController.getPerson()
    │                   └── simulateLatency() [500ms]
```

No Jaeger UI, você verá:
- **person-api** como serviço principal
- **mock-api** como serviço chamado
- Trace completo mostrando a latência de 500ms do mock-api

## Verificar Traces

1. Acesse o Jaeger UI: http://localhost:16686
2. Selecione o serviço `person-api`
3. Clique em "Find Traces"
4. Você verá traces completos incluindo chamadas ao `mock-api`

## Troubleshooting

### Erro: "OpenTelemetry Java Agent not found"

Certifique-se de que o arquivo `opentelemetry-javaagent.jar` está na raiz do projeto principal (não no diretório mock-api).

### Traces não aparecem

1. Verifique se o OpenTelemetry Collector está rodando:
   ```bash
   docker ps | grep otel-collector
   ```

2. Verifique se o Collector está recebendo traces:
   ```bash
   docker logs otel-collector --tail 20
   ```

3. Verifique se ambas as aplicações estão usando o mesmo endpoint do Collector

