# Métricas do HTTP Client Connection Pool

Este documento descreve as métricas expostas do Apache HttpClient Connection Pool usado pelo Feign.

## Métricas Disponíveis

Todas as métricas estão disponíveis no endpoint `/actuator/prometheus` e podem ser visualizadas no Grafana.

### 1. `httpclient.pool.max.total`
- **Tipo**: Gauge
- **Descrição**: Número máximo de conexões totais configuradas no pool
- **Valor**: Inteiro (ex: 300)
- **Uso**: Indica o limite máximo do pool

### 2. `httpclient.pool.available`
- **Tipo**: Gauge
- **Descrição**: Número de conexões disponíveis (livres) no pool
- **Valor**: Inteiro (ex: 250)
- **Uso**: Conexões que podem ser usadas imediatamente

### 3. `httpclient.pool.leased`
- **Tipo**: Gauge
- **Descrição**: Número de conexões em uso (leased) no pool
- **Valor**: Inteiro (ex: 50)
- **Uso**: Conexões atualmente sendo usadas por requisições

### 4. `httpclient.pool.pending`
- **Tipo**: Gauge
- **Descrição**: Número de requisições pendentes aguardando uma conexão
- **Valor**: Inteiro (ex: 0)
- **Uso**: Requisições que estão esperando uma conexão ficar disponível
- **⚠️ Alerta**: Se > 0, indica que o pool está saturado

### 5. `httpclient.pool.utilization.ratio`
- **Tipo**: Gauge
- **Descrição**: Taxa de utilização do pool (0.0 a 1.0)
- **Valor**: Double (ex: 0.166 = 16.6% de utilização)
- **Cálculo**: `leased / max`
- **Uso**: Monitorar utilização do pool

### 6. `httpclient.pool.utilization.percent`
- **Tipo**: Gauge
- **Descrição**: Taxa de utilização do pool em percentual (0 a 100)
- **Valor**: Double (ex: 16.6 = 16.6% de utilização)
- **Cálculo**: `(leased / max) * 100`
- **Uso**: Mais fácil de ler que o ratio

### 7. `httpclient.pool.total`
- **Tipo**: Gauge
- **Descrição**: Total de conexões no pool (available + leased)
- **Valor**: Inteiro (ex: 300)
- **Cálculo**: `available + leased`
- **Uso**: Verificar se todas as conexões estão sendo usadas

### 8. `httpclient.pool.max.per.route`
- **Tipo**: Gauge
- **Descrição**: Número máximo de conexões por rota (por host)
- **Valor**: Inteiro (ex: 300)
- **Uso**: Limite de conexões por host específico

## Relações Importantes

```
max.total = available + leased (quando pool está cheio)
utilization.percent = (leased / max.total) * 100
```

## Como Visualizar

### 1. Via Prometheus

Acesse: `http://localhost:9090` e execute queries como:

```promql
# Taxa de utilização do pool
httpclient_pool_utilization_percent

# Conexões disponíveis
httpclient_pool_available

# Conexões em uso
httpclient_pool_leased

# Requisições pendentes (alerta!)
httpclient_pool_pending
```

### 2. Via Grafana

As métricas estarão disponíveis automaticamente. Você pode criar dashboards com:

- **Gráfico de linha**: Taxa de utilização ao longo do tempo
- **Gauge**: Conexões disponíveis vs. máximas
- **Tabela**: Todas as métricas do pool
- **Alerta**: Se `pending > 0` ou `utilization.percent > 90`

### 3. Via Actuator Metrics

Acesse: `http://localhost:8080/actuator/metrics/httpclient.pool.utilization.percent`

## Exemplo de Dashboard Grafana

### Query para Taxa de Utilização:
```promql
httpclient_pool_utilization_percent{application="person-api"}
```

### Query para Conexões Disponíveis:
```promql
httpclient_pool_available{application="person-api"}
```

### Query para Conexões em Uso:
```promql
httpclient_pool_leased{application="person-api"}
```

### Query para Requisições Pendentes (Alerta):
```promql
httpclient_pool_pending{application="person-api"}
```

## Alertas Recomendados

1. **Pool Saturado**: `httpclient_pool_pending > 0`
   - Indica que requisições estão esperando conexões
   - Ação: Aumentar `feign.httpclient.max-connections`

2. **Alta Utilização**: `httpclient_pool_utilization_percent > 90`
   - Pool quase esgotado
   - Ação: Monitorar e considerar aumentar o pool

3. **Pool Esgotado**: `httpclient_pool_available == 0` e `httpclient_pool_leased == max`
   - Todas as conexões estão em uso
   - Ação: Aumentar `feign.httpclient.max-connections`

## Configuração Atual

No `application.properties`:
```properties
feign.httpclient.max-connections=300
feign.httpclient.max-connections-per-route=300
```

Essas configurações determinam os valores de `max.total` e `max.per.route`.

