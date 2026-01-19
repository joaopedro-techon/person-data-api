# Tracing Distribuído - Person API + Mock API

Este projeto está configurado para tracing distribuído de ponta a ponta, rastreando requisições desde a Person API até o Mock API.

## 🎯 Arquitetura de Tracing

```
Cliente
    ↓ HTTP Request
person-api (porta 8080)
    ├── PersonController.getPerson()
    │   └── PersonService.getPersonById()
    │       └── ExternalPersonClient.getExternalPerson() [Feign]
    │           ↓ HTTP Request
    │           └── mock-api (porta 8089)
    │               └── MockPersonController.getPerson()
    │                   └── simulateLatency() [500ms]
    │
    ↓ Traces enviados via OpenTelemetry
OpenTelemetry Collector (porta 4317)
    ├── Tail-Based Sampling (>100ms ou erro)
    └── Jaeger (porta 14250)
        └── Jaeger UI (porta 16686)
```

## 🚀 Como Executar com Tracing Completo

### 1. Baixar o OpenTelemetry Java Agent

**Na raiz do projeto:**
```bash
# Windows
.\download-otel-agent.bat

# Linux/Mac
chmod +x download-otel-agent.sh
./download-otel-agent.sh
```

### 2. Iniciar o OpenTelemetry Collector e Jaeger

```bash
docker-compose up -d otel-collector jaeger
```

### 3. Compilar as Aplicações

**Person API:**
```bash
mvn clean package
```

**Mock API:**
```bash
cd mock-api
mvn clean package
cd ..
```

### 4. Executar as Aplicações com OpenTelemetry

**Terminal 1 - Mock API:**
```bash
cd mock-api
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

**Terminal 2 - Person API:**
```bash
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

### 5. Fazer Requisições de Teste

```bash
# Requisição normal
curl http://localhost:8080/api/person/1

# Requisição que será rastreada (latência > 100ms devido ao delay de 500ms)
curl http://localhost:8080/api/person/2
```

### 6. Visualizar Traces no Jaeger

1. Acesse: http://localhost:16686
2. Selecione o serviço: `person-api`
3. Clique em "Find Traces"
4. Você verá traces completos mostrando:
   - Requisição HTTP em `person-api`
   - Chamada Feign para `mock-api`
   - Processamento no `mock-api` (500ms de latência)
   - Resposta completa

## 📊 O que Você Verá no Jaeger

### Trace Completo

```
GET /api/person/1
├── person-api: HTTP GET /api/person/1
│   ├── person-api: PersonController.getPerson()
│   │   └── person-api: PersonService.getPersonById()
│   │       └── person-api: ExternalPersonClient.getExternalPerson()
│   │           └── HTTP GET http://mock-api:8089/external-person/1
│   │               └── mock-api: HTTP GET /external-person/1
│   │                   └── mock-api: MockPersonController.getPerson()
│   │                       └── mock-api: simulateLatency() [500ms]
```

### Informações Visíveis

- ✅ **Tempo total** da requisição
- ✅ **Tempo em cada serviço** (person-api vs mock-api)
- ✅ **Latência de 500ms** do mock-api claramente visível
- ✅ **Chamadas HTTP** entre serviços
- ✅ **Erros** (se houver)

## ⚙️ Configuração

### Person API
- **Service Name**: `person-api`
- **Collector**: `http://localhost:4317` (OTLP gRPC)
- **Script**: `run-with-otel.bat` / `run-with-otel.sh`

### Mock API
- **Service Name**: `mock-api`
- **Collector**: `http://localhost:4317` (OTLP gRPC)
- **Script**: `mock-api/run-with-otel.bat` / `mock-api/run-with-otel.sh`

### OpenTelemetry Collector
- **Configuração**: `otel-collector/otel-collector-config.yaml`
- **Sampling**: Apenas traces com erro ou duração > 100ms
- **Endpoint**: `http://localhost:4317` (gRPC) ou `http://localhost:4318` (HTTP)

## 🔍 Troubleshooting

### Traces não aparecem no Jaeger

1. **Verificar se o Collector está rodando:**
   ```bash
   docker ps | grep otel-collector
   ```

2. **Verificar logs do Collector:**
   ```bash
   docker logs otel-collector --tail 20
   ```

3. **Verificar se ambas aplicações estão rodando:**
   - Person API na porta 8080
   - Mock API na porta 8089

4. **Verificar conectividade:**
   ```bash
   # Testar Collector
   curl http://localhost:4318
   
   # Testar Person API
   curl http://localhost:8080/api/person/1
   
   # Testar Mock API
   curl http://localhost:8089/external-person/1
   ```

### Erro: "Connection refused" na porta 4317/4318

- Verifique se o Collector está rodando: `docker-compose up -d otel-collector`
- Aguarde alguns segundos para o Collector inicializar
- Verifique se a porta não está em uso: `netstat -ano | findstr :4317`

### Apenas um serviço aparece no Jaeger

- Certifique-se de que ambas as aplicações estão usando o OpenTelemetry Agent
- Verifique se ambas estão apontando para o mesmo Collector (`localhost:4317`)
- Verifique os logs de ambas as aplicações para erros

## 📚 Recursos Adicionais

- [OpenTelemetry Java Agent](https://opentelemetry.io/docs/instrumentation/java/automatic/)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)
- [OpenTelemetry Collector](https://opentelemetry.io/docs/collector/)

