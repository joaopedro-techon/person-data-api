# Spans Explícitos no Mock API

O Mock API agora cria **spans explícitos** usando a API do OpenTelemetry, permitindo ver operações detalhadas no trace.

## 📊 Spans Criados

No endpoint `/external-person/{id}`, os seguintes spans são criados:

### 1. Span Principal: `mock-api.getPerson`
- **Atributos**:
  - `person.id`: ID da pessoa
  - `operation.type`: "person.lookup"
  - `response.status`: "success" ou "error"
- **Eventos**:
  - "Starting person lookup"
  - "Person lookup completed successfully"

### 2. Span Filho: `mock-api.simulateLatency`
- **Atributos**:
  - `latency.ms`: 500
- **Eventos**:
  - "Latency simulation completed"
- **Duração**: ~500ms

### 3. Span Filho: `mock-api.buildResponse`
- **Atributos**:
  - `response.type`: "PersonResponse"
  - `person.name`: Nome da pessoa
  - `person.age`: Idade da pessoa
- **Eventos**:
  - "Response built successfully"

## 🎯 Como Aparece no Jaeger

No Jaeger UI, você verá uma hierarquia de spans:

```
mock-api: HTTP GET /external-person/1
  └── mock-api.getPerson
      ├── mock-api.simulateLatency [500ms]
      └── mock-api.buildResponse [<1ms]
```

## 🔍 Detalhes Visíveis

Cada span mostra:
- ✅ **Nome da operação** (ex: "mock-api.getPerson")
- ✅ **Atributos customizados** (person.id, latency.ms, etc.)
- ✅ **Eventos** (timestamps de eventos importantes)
- ✅ **Duração** de cada operação
- ✅ **Erros** (se houver, com stack trace)

## 📝 Exemplo de Uso

Após executar o Mock API com OpenTelemetry:

```bash
cd mock-api
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

E fazer uma requisição:

```bash
curl http://localhost:8080/api/person/1
```

No Jaeger UI (http://localhost:16686), você verá:
1. O span principal da requisição HTTP
2. O span `mock-api.getPerson` com todos os atributos
3. O span `mock-api.simulateLatency` mostrando os 500ms
4. O span `mock-api.buildResponse` mostrando a construção da resposta

## 🎨 Personalização

Você pode adicionar mais spans em outros métodos ou adicionar mais atributos:

```java
Span customSpan = tracer.spanBuilder("minha.operacao")
    .setAttribute("custom.attr", "valor")
    .startSpan();

try (Scope scope = customSpan.makeCurrent()) {
    // Seu código aqui
    customSpan.addEvent("Evento importante");
} finally {
    customSpan.end();
}
```

