resilience4j.circuitbreaker.instances.consultas-por-operacao-db.record-exceptions[0]=org.hibernate.exception.JDBCConnectionException
resilience4j.circuitbreaker.instances.consultas-por-operacao-db.record-exceptions[1]=java.sql.SQLTransientConnectionException
resilience4j.circuitbreaker.instances.consultas-por-operacao-db.record-exceptions[2]=org.springframework.jdbc.CannotGetJdbcConnectionException
resilience4j.circuitbreaker.instances.consultas-por-operacao-db.record-exceptions[3]=org.springframework.dao.DataAccessResourceFailureException
# Person API - Spring Boot com Prometheus e Grafana

Projeto Spring Boot com Java 11 que expõe um endpoint GET para retornar dados de uma pessoa, utilizando Feign para chamadas a API externa mockada com WireMock, e métricas expostas via Prometheus e visualizadas no Grafana.

## Tecnologias

- Java 11
- Spring Boot 2.7.18
- Spring Cloud OpenFeign
- WireMock (para mock da API externa)
- Spring Boot Actuator
- Micrometer Prometheus
- Prometheus
- Grafana

## Estrutura do Projeto

```
.
├── src/
│   ├── main/
│   │   ├── java/com/example/person/
│   │   │   ├── api/
│   │   │   │   ├── controller/PersonController.java
│   │   │   │   └── dto/PersonDto.java
│   │   │   ├── config/MetricsConfig.java
│   │   │   ├── external/ExternalPersonClient.java
│   │   │   ├── service/PersonService.java
│   │   │   └── PersonApiApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── prometheus/
│   └── prometheus.yml
├── grafana/
│   ├── provisioning/
│   │   ├── datasources/prometheus.yml
│   │   └── dashboards/dashboard.yml
│   └── dashboards/person-api-dashboard.json
├── wiremock/
│   └── mappings/person-mapping.json
├── docker-compose.yml
└── pom.xml
```

## Como Executar

### 1. Iniciar os serviços (WireMock, Prometheus e Grafana)

```bash
docker-compose up -d
```

### 2. Executar a aplicação Spring Boot

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### 3. Acessar os serviços

- **API**: http://localhost:8080
- **WireMock**: http://localhost:8089
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
  - Usuário: `admin`
  - Senha: `admin`

## Endpoints

### GET /api/person/{id}

Retorna os dados de uma pessoa pelo ID.

**Exemplo:**
```bash
curl http://localhost:8080/api/person/1
```

**Resposta:**
```json
{
  "id": 1,
  "name": "João Silva",
  "age": 30,
  "email": "joao.silva@example.com"
}
```

### Métricas (Actuator)

- **Health**: http://localhost:8080/actuator/health
- **Prometheus**: http://localhost:8080/actuator/prometheus
- **Metrics**: http://localhost:8080/actuator/metrics

## Métricas Expostas

### Threads
- `jvm.threads.count` - Quantidade de threads por estado
- `jvm.threads.peak` - Pico de threads
- `jvm.threads.daemon` - Threads daemon

### Endpoint
- `person.endpoint.requests.total` - Total de requisições
- `person.endpoint.errors.total` - Total de erros
- `person.endpoint.response.time` - Tempo de resposta

## Dashboard Grafana

O dashboard inclui:
1. **Threads por Estado** - Visualização de threads agrupadas por estado
2. **Total de Threads** - Contador total de threads
3. **Threads Disponíveis (Group by Estado)** - Gráfico de barras com threads por estado
4. **Tempo de Resposta do Endpoint** - Gráfico com tempo médio, P95 e P99
5. **Quantidade de Chamadas** - Taxa e total de requisições
6. **Quantidade de Erros** - Taxa e total de erros
7. **Taxa de Erro (%)** - Percentual de erros

O dashboard é carregado automaticamente ao iniciar o Grafana via docker-compose.

## Configuração

### application.properties

- `external.person-api.url`: URL da API externa (WireMock)
- Endpoints do Actuator configurados para expor métricas Prometheus

### Prometheus

Configurado para fazer scrape da aplicação em `host.docker.internal:8080` (para Windows/Mac) ou `localhost:8080` (Linux).

**Nota para Linux**: Se estiver usando Linux, você pode precisar alterar `host.docker.internal` para o IP da sua máquina no arquivo `prometheus/prometheus.yml`.

## Desenvolvimento

### Executar testes

```bash
mvn test
```

### Build

```bash
mvn clean package
```

## Observações

- O WireMock está configurado para responder na porta 8089
- O Prometheus faz scrape a cada 15 segundos
- O Grafana atualiza o dashboard a cada 10 segundos
- As métricas de threads são coletadas automaticamente via JMX

