# Configuração do Jaeger - Distributed Tracing com OpenTelemetry Java Agent

Este projeto está instrumentado com **Jaeger** usando o **OpenTelemetry Java Agent** para tracing distribuído. O agent faz instrumentação automática sem necessidade de modificar o código.

## 📋 O que é Jaeger?

Jaeger é uma plataforma de tracing distribuído open-source, originalmente criada pela Uber. Ele ajuda a:

- **Rastrear requisições** através de múltiplos serviços
- **Identificar gargalos** de performance
- **Visualizar dependências** entre serviços
- **Debugar problemas** em sistemas distribuídos

## 🚀 Como Usar

### 1. Baixar o OpenTelemetry Java Agent

**Windows:**
```bash
.\download-otel-agent.bat
```

**Linux/Mac:**
```bash
chmod +x download-otel-agent.sh
./download-otel-agent.sh
```

Isso baixará o arquivo `opentelemetry-javaagent.jar` na raiz do projeto.

### 2. Iniciar o Jaeger

O Jaeger está configurado no `docker-compose.yml`. Para iniciá-lo:

```bash
docker-compose up -d jaeger
```

Ou iniciar todos os serviços:

```bash
docker-compose up -d
```

### 3. Acessar a UI do Jaeger

Após iniciar, acesse a interface web do Jaeger:

```
http://localhost:16686
```

### 4. Compilar a Aplicação

Primeiro, compile a aplicação:

```bash
mvn clean package
```

### 5. Executar a Aplicação com o Agent

**Windows:**
```bash
.\run-with-otel.bat
```

**Linux/Mac:**
```bash
chmod +x run-with-otel.sh
./run-with-otel.sh
```

**Ou manualmente:**

```bash
java \
  -javaagent:opentelemetry-javaagent.jar \
  -Dotel.service.name=person-api \
  -Dotel.traces.exporter=otlp \
  -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
  -Dotel.metrics.exporter=none \
  -jar target/person-api-0.0.1-SNAPSHOT.jar
```

### 6. Gerar Traces

Faça algumas requisições para gerar traces:

```bash
curl http://localhost:8080/api/person/1
curl http://localhost:8080/api/person/2
curl http://localhost:8080/api/person/3
```

### 7. Visualizar Traces no Jaeger UI

1. Acesse `http://localhost:16686`
2. Selecione o serviço `person-api` no dropdown
3. Clique em "Find Traces"
4. Você verá todos os traces gerados

## 🔍 O que é Rastreado Automaticamente?

O OpenTelemetry Java Agent faz instrumentação automática de:

- ✅ **Spring MVC** - Requisições HTTP recebidas
- ✅ **Feign Client** - Chamadas HTTP para APIs externas
- ✅ **Apache HttpClient** - Requisições HTTP (usado pelo Feign)
- ✅ **JDBC** - Consultas ao banco de dados (se houver)
- ✅ **Logback/Log4j** - Logs estruturados
- ✅ **E muito mais!** - Veja a [lista completa](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/docs/supported-libraries.md)

## 📊 Estrutura de um Trace

Um trace completo de uma requisição `/api/person/{id}` contém:

```
GET /api/person/1
├── HTTP GET /api/person/1 (Spring MVC)
│   └── PersonController.getPerson()
│       └── PersonService.getPersonById()
│           └── ExternalPersonClient.getExternalPerson() (Feign)
│               └── HTTP GET http://external-api/external-person/1 (Apache HttpClient)
```

Cada nível representa um **span** que mostra:
- **Tempo de execução**
- **Tags** (método HTTP, URL, status code, etc.)
- **Logs** (erros, eventos importantes)

## ⚙️ Configuração

### Variáveis de Ambiente

Você pode configurar o OpenTelemetry via variáveis de ambiente:

```bash
# Nome do serviço
export OTEL_SERVICE_NAME=person-api

# Exportador de traces (otlp para Jaeger)
export OTEL_TRACES_EXPORTER=otlp

# Endpoint do Jaeger (OTLP gRPC)
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

# Desabilitar métricas (opcional)
export OTEL_METRICS_EXPORTER=none

# Desabilitar logs (opcional)
export OTEL_LOGS_EXPORTER=none
```

### Parâmetros JVM

Você também pode configurar via parâmetros JVM (usado nos scripts):

```bash
-javaagent:opentelemetry-javaagent.jar
-Dotel.service.name=person-api
-Dotel.traces.exporter=otlp
-Dotel.exporter.otlp.endpoint=http://localhost:4317
-Dotel.metrics.exporter=none
-Dotel.logs.exporter=none
```

### Sampling (Taxa de Amostragem)

Para controlar quantos traces são enviados, use:

```bash
# 100% dos traces (desenvolvimento)
-Dotel.traces.sampler=traceidratio
-Dotel.traces.sampler.arg=1.0

# 10% dos traces (produção - recomendado)
-Dotel.traces.sampler=traceidratio
-Dotel.traces.sampler.arg=0.1

# 1% dos traces (alta carga)
-Dotel.traces.sampler=traceidratio
-Dotel.traces.sampler.arg=0.01
```

## 🔧 Troubleshooting

### Traces não aparecem no Jaeger UI

1. **Verifique se o Jaeger está rodando:**
   ```bash
   docker ps | grep jaeger
   ```

2. **Verifique os logs do Jaeger:**
   ```bash
   docker logs jaeger
   ```

3. **Verifique se o agent foi baixado:**
   ```bash
   ls -la opentelemetry-javaagent.jar
   ```

4. **Verifique se a aplicação está usando o agent:**
   - Procure por logs como "OpenTelemetry Java Instrumentation" no console
   - Verifique se `-javaagent` está presente nos argumentos JVM

5. **Verifique a conectividade:**
   ```bash
   # Teste se consegue conectar ao Jaeger
   curl http://localhost:16686/api/services
   ```

### Erro: "Connection refused"

- Verifique se o Jaeger está rodando: `docker-compose up -d jaeger`
- Verifique se a porta 4317 está acessível
- Verifique se `OTEL_EXPORTER_OTLP_ENDPOINT` está correto

### Agent não está instrumentando

- Verifique se o `-javaagent` está antes do `-jar`
- Verifique se o caminho do agent está correto
- Verifique se a versão do Java é compatível (Java 8+)

## 📚 Recursos Adicionais

- [Documentação Oficial do Jaeger](https://www.jaegertracing.io/docs/)
- [OpenTelemetry Java Instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation)
- [OpenTelemetry Java Agent](https://opentelemetry.io/docs/instrumentation/java/automatic/)
- [Bibliotecas Suportadas](https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/docs/supported-libraries.md)

## 🎯 Vantagens do Java Agent

✅ **Zero código** - Instrumentação automática sem modificar código  
✅ **Performance** - Overhead mínimo (~1-3%)  
✅ **Completo** - Instrumenta automaticamente Spring, Feign, HTTP, JDBC, etc.  
✅ **Padrão da indústria** - Usado por grandes empresas  
✅ **Atualizações fáceis** - Basta atualizar o JAR do agent  

## 🔄 Migração de Configuração Manual

Se você estava usando configuração manual do Jaeger:

- ❌ Removido: `JaegerConfig.java` (não é mais necessário)
- ❌ Removido: Dependências `jaeger-client` e `opentracing-*` (não são mais necessárias)
- ✅ Adicionado: OpenTelemetry Java Agent (instrumentação automática)
- ✅ Mantido: Jaeger no Docker (recebe traces via OTLP)
