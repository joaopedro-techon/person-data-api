# Teste de Burst - 6000 Clientes Simultâneos

Este diretório contém testes JMeter configurados para simular um **burst real** com 6000 clientes fazendo chamadas ao mesmo tempo, usando estratégia **Open Model**.

## Arquivos Disponíveis

### 1. `person-api-burst-standard.jmx` ⭐ RECOMENDADO
- **Tipo**: Thread Group padrão do JMeter
- **Clientes**: 6000 threads simultâneas
- **Ramp-up**: 5 segundos (burst rápido)
- **Duração**: 300 segundos (5 minutos)
- **Vantagem**: Funciona sem plugins adicionais
- **Modelo**: Simula burst onde todos os clientes chegam quase simultaneamente

### 2. `person-api-burst-6000.jmx`
- **Tipo**: Concurrency Thread Group (requer plugin)
- **Concorrência**: 6000 clientes simultâneos
- **Ramp-up**: 5 segundos
- **Hold**: 300 segundos
- **Vantagem**: Melhor controle de concorrência (Open Model)
- **Requer**: Plugin "Concurrency Thread Group" do BlazeMeter

### 3. `person-api-burst-test.jmx`
- **Tipo**: Ultimate Thread Group (requer plugin)
- **Alternativa**: Se você já tiver o plugin instalado

## Pré-requisitos

### Para `person-api-burst-standard.jmx`:
- ✅ Apache JMeter 5.6+ (sem plugins adicionais necessários)

### Para `person-api-burst-6000.jmx`:
- Apache JMeter 5.6+
- Plugin: **Concurrency Thread Group** (BlazeMeter)
  - Instalar via: `Plugins Manager` → `Concurrency Thread Group`

## Como Executar

### Opção 1: Interface Gráfica (Recomendado para primeira execução)

1. Abra o JMeter
2. **File** → **Open** → Selecione `person-api-burst-standard.jmx`
3. **Run** → **Start** (ou Ctrl+R)
4. Monitore os resultados nos listeners

### Opção 2: Linha de Comando (Melhor Performance)

```bash
# Windows
jmeter -n -t person-api-burst-standard.jmx -l burst-results.jtl -e -o burst-report/

# Linux/Mac
./jmeter -n -t person-api-burst-standard.jmx -l burst-results.jtl -e -o burst-report/
```

## Configuração do Teste

### person-api-burst-standard.jmx

- **Threads**: 6000
- **Ramp-up**: 5 segundos (burst rápido - todos chegam quase ao mesmo tempo)
- **Duração**: 300 segundos (5 minutos)
- **Endpoint**: `GET /api/person/{id}` (ID aleatório entre 1-1000)
- **Timeout**: 30 segundos
- **Keep-Alive**: Habilitado

### Comportamento do Burst

1. **0-5s**: Ramp-up rápido - 6000 threads iniciando
2. **5-305s**: Todas as 6000 threads ativas simultaneamente
3. **305s+**: Teste finaliza

## O que Observar

### Durante o Teste

1. **Throughput**: Requisições por segundo (deve aumentar rapidamente)
2. **Response Time**: Latência média, P95, P99
3. **Error Rate**: Taxa de erros (timeouts, 500, etc.)
4. **Active Threads**: Deve chegar a 6000 rapidamente

### Métricas Importantes

- **Burst Impact**: Como a aplicação lida com pico súbito de carga
- **Recovery Time**: Tempo para estabilizar após o burst
- **Thread Pool**: Se o pool de threads do Tomcat aguenta
- **Connection Pool**: Se o pool do Feign HttpClient aguenta
- **Memory**: Uso de memória durante o burst

## Monitoramento

### Actuator Metrics
```
http://localhost:8080/actuator/metrics/tomcat.threads.busy
http://localhost:8080/actuator/metrics/tomcat.threads.current
http://localhost:8080/actuator/metrics/feign.httpclient.pool.leased
http://localhost:8080/actuator/metrics/feign.httpclient.pool.pending
```

### Prometheus
```
http://localhost:9090
```

Queries úteis:
- `rate(person_endpoint_requests_total[1m])` - Throughput
- `histogram_quantile(0.95, person_endpoint_response_time_bucket)` - P95
- `tomcat_threads_busy` - Threads ocupadas

### Grafana
```
http://localhost:3000
```

## Ajustando o Teste

### Aumentar/Diminuir Clientes Simultâneos

Edite o arquivo `.jmx` e altere:
- `ThreadGroup.num_threads`: Número de threads (ex: 10000 para 10k clientes)

### Ajustar Ramp-up (Velocidade do Burst)

- **Burst mais rápido**: Reduza `ThreadGroup.ramp_time` para 1-2 segundos
- **Burst mais suave**: Aumente para 10-15 segundos

### Ajustar Duração

- `ThreadGroup.duration`: Duração em segundos (ex: 600 para 10 minutos)

## Diferenças: Open Model vs Closed Model

### Open Model (Este Teste)
- ✅ Simula chegada de clientes (arrivals)
- ✅ Mais realista para cenários de burst
- ✅ Clientes chegam independentemente
- ✅ Melhor para testar capacidade de pico

### Closed Model (Teste Anterior)
- ❌ Limita número de threads fixas
- ❌ Não simula burst real
- ❌ Threads esperam resposta antes de próxima requisição

## Dicas

1. **Primeira Execução**: Use GUI para ver comportamento
2. **Testes Sérios**: Use modo não-GUI (`-n`) para melhor performance
3. **Desabilite Listeners**: Em produção, desabilite "View Results Tree" para melhor performance
4. **Monitore Recursos**: Acompanhe CPU, memória e threads durante o teste
5. **Comece Menor**: Se 6000 for muito, teste com 1000 primeiro

## Troubleshooting

### Erro: "Out of Memory"
- Aumente heap do JMeter: `set HEAP=-Xms1g -Xmx4g`
- Reduza número de threads

### Erro: "Connection Refused"
- Verifique se a aplicação está rodando em `localhost:8080`
- Verifique se Mock API está rodando em `localhost:8089`

### Performance Lenta do JMeter
- Use modo não-GUI (`-n`)
- Desabilite listeners desnecessários
- Execute em máquina com mais recursos

## Resultados Esperados

Com 6000 clientes simultâneos:
- **Throughput**: Depende da latência da API externa (500ms)
  - Teórico máximo: ~12000 req/s (6000 clientes / 0.5s)
  - Prático: Menor devido a overhead
- **Response Time**: P95 deve estar próximo de 500-600ms (latência da API externa)
- **Threads Tomcat**: Devem aumentar para lidar com carga
- **Pool Feign**: Deve estar próximo do máximo configurado (200)

