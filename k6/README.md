# Testes de Carga K6 - Person API

Este diretório contém scripts de teste de carga usando [K6](https://k6.io/) para simular alta carga na Person API.

## 📋 Pré-requisitos

### Instalar K6

**Windows:**
```powershell
# Opção 1: Chocolatey
choco install k6

# Opção 2: Scoop
scoop install k6

# Opção 3: Download direto
# Baixe de: https://github.com/grafana/k6/releases
# Extraia e adicione ao PATH
```

**Linux:**
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg \
  --keyserver hkp://keyserver.ubuntu.com:80 \
  --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69

echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | \
  sudo tee /etc/apt/sources.list.d/k6.list

sudo apt-get update
sudo apt-get install k6
```

**macOS:**
```bash
brew install k6
```

**Verificar instalação:**
```bash
k6 version
```

## 🚀 Testes Disponíveis

### 1. Teste de Alta Carga: 6000 TPS por 30 minutos

#### Executar o teste

```bash
# Teste básico (usa http://localhost:8080)
k6 run person-api-6000-tps.js

# Com URL customizada
k6 run -e BASE_URL=http://localhost:8080 person-api-6000-tps.js

# Com URL remota
k6 run -e BASE_URL=http://192.168.1.100:8080 person-api-6000-tps.js
```

#### Perfil do Teste

- **Ramp-up**: 0 → 6000 TPS em 6 minutos (gradual)
- **Carga Sustentada**: 6000 TPS por 30 minutos
- **Ramp-down**: 6000 → 0 TPS em 2 minutos
- **Duração Total**: ~38 minutos
- **Máximo de VUs**: 10000 (para suportar 6000 TPS)

#### Estrutura do Teste

```
Minuto 0-1:   0 → 1000 TPS
Minuto 1-2:   1000 → 2000 TPS
Minuto 2-3:   2000 → 3000 TPS
Minuto 3-4:   3000 → 4000 TPS
Minuto 4-5:   4000 → 5000 TPS
Minuto 5-6:   5000 → 6000 TPS
Minuto 6-36:  6000 TPS (sustentado)
Minuto 36-37: 6000 → 3000 TPS
Minuto 37-38: 3000 → 0 TPS
```

---

### 2. Teste Otimizado: 1000 TPS com Menor Quantidade de VUs

#### Executar o teste

```bash
# Teste básico (usa http://localhost:8080)
k6 run person-api-1000-tps.js

# Com URL customizada
k6 run -e BASE_URL=http://localhost:8080 person-api-1000-tps.js

# Com URL remota
k6 run -e BASE_URL=http://192.168.1.100:8080 person-api-1000-tps.js
```

#### Perfil do Teste

- **Ramp-up**: 0 → 1000 TPS em 3 minutos (gradual)
- **Carga Sustentada**: 1000 TPS por 15 minutos
- **Ramp-down**: 1000 → 0 TPS em 1 minuto
- **Duração Total**: ~20 minutos
- **VUs Iniciais**: 50 (menor quantidade)
- **Máximo de VUs**: 2000 (otimizado para 1000 TPS)

#### Estrutura do Teste

```
0-30s:        0 → 250 TPS
30s-1m:       250 → 500 TPS
1m-1m30s:    500 → 750 TPS
1m30s-2m:    750 → 1000 TPS
2m-3m:       1000 TPS (estabilização)
3m-18m:      1000 TPS (sustentado por 15 minutos)
18m-18m30s:  1000 → 500 TPS
18m30s-19m:  500 → 0 TPS
```

#### Vantagens deste Teste

- ✅ **Menor consumo de recursos**: Usa menos VUs (50-2000 vs 100-10000)
- ✅ **Mais rápido**: Duração menor (20min vs 38min)
- ✅ **Ideal para validação**: Perfeito para testar antes do teste de alta carga
- ✅ **Eficiente**: K6 ajusta automaticamente o número de VUs para atingir 1000 TPS

## 📊 Métricas Coletadas

### Métricas Automáticas do K6

- **`http_req_duration`**: Tempo total da requisição (p50, p75, p95, p99)
- **`http_req_waiting`**: Tempo de espera (TTFB - Time To First Byte)
- **`http_req_connecting`**: Tempo de conexão TCP
- **`http_req_sending`**: Tempo de envio da requisição
- **`http_req_receiving`**: Tempo de recebimento da resposta
- **`http_req_failed`**: Taxa de falhas HTTP
- **`http_reqs`**: Total de requisições e taxa (TPS)
- **`vus`**: Usuários virtuais ativos
- **`vus_max`**: Máximo de VUs usados
- **`iterations`**: Total de iterações completadas

### Métricas Customizadas

- **`errors`**: Taxa de erros (checks falhados)
- **`response_time`**: Tempo de resposta customizado
- **`total_requests`**: Contador total de requisições

## 🎯 Thresholds (Limites de Sucesso)

### Teste 6000 TPS

O teste **falha** se:

- ❌ **50% das requisições** > 500ms
- ❌ **75% das requisições** > 1 segundo
- ❌ **95% das requisições** > 2 segundos
- ❌ **99% das requisições** > 5 segundos
- ❌ **Taxa de erros HTTP** > 5%
- ❌ **Taxa de erros (checks)** > 5%
- ❌ **Throughput** < 5000 req/s (permite margem de 1000)

### Teste 1000 TPS

O teste **falha** se:

- ❌ **50% das requisições** > 500ms
- ❌ **75% das requisições** > 1 segundo
- ❌ **95% das requisições** > 2 segundos
- ❌ **99% das requisições** > 5 segundos
- ❌ **Taxa de erros HTTP** > 5%
- ❌ **Taxa de erros (checks)** > 5%
- ❌ **Throughput** < 900 req/s (permite margem de 100)

## 📈 Saída de Exemplo

```
          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: person-api-6000-tps.js
     output: -

  scenarios: (100.0%) 1 scenario, 10000 max VUs, 38m0s max duration
           ✓ gradual_6000_tps: 6000.00 iters/s for 30m0s, 10000 max VUs

     data_received..................: 1.2 GB  530 kB/s
     data_sent......................: 45 MB   20 kB/s
     http_req_blocked...............: avg=1.2ms   min=0s     med=0s      max=2.5s
     http_req_connecting............: avg=0.5ms   min=0s     med=0s      max=1.2s
     http_req_duration..............: avg=250ms   min=50ms   med=200ms   max=3.5s
       { expected_response:true }...: avg=250ms   min=50ms   med=200ms   max=3.5s
     http_req_failed................: 0.00%  ✓ 0%
     http_req_receiving.............: avg=2ms    min=0s     med=1ms     max=50ms
     http_req_sending...............: avg=0.1ms  min=0s     med=0s      max=5ms
     http_req_waiting...............: avg=248ms  min=48ms   med=198ms   max=3.45s
     http_reqs......................: 10800000 6000.00/s  ✓ 6000.00/s
     iteration_duration.............: avg=250ms  min=50ms   med=200ms   max=3.5s
     iterations.....................: 10800000
     vus............................: 6000     min=100     max=8500
     vus_max........................: 10000    min=100     max=10000

     ✓ status is 200
     ✓ response time < 5s
     ✓ has person data
     ✓ content type is json

     checks.........................: 100.00% ✓ 43200000  ✗ 0
     data_received..................: 1.2 GB  530 kB/s
     data_sent......................: 45 MB   20 kB/s
     errors.........................: 0.00%   ✓ 0%
     response_time..................: avg=250ms  min=50ms   med=200ms   max=3.5s
     total_requests.................: 10800000
```

## 🔧 Opções de Saída

### Console (padrão)
```bash
k6 run person-api-6000-tps.js
```

### JSON (para análise posterior)
```bash
k6 run --out json=results.json person-api-6000-tps.js
```

### CSV (para planilhas)
```bash
k6 run --out csv=results.csv person-api-6000-tps.js
```

### InfluxDB (para Grafana)
```bash
k6 run --out influxdb=http://localhost:8086/k6 person-api-6000-tps.js
```

### Prometheus (via k6-prometheus)
```bash
# Requer plugin: https://github.com/grafana/xk6-prometheus-rw
k6 run --out experimental-prometheus-rw person-api-6000-tps.js
```

### Múltiplas saídas
```bash
k6 run --out json=results.json --out csv=results.csv person-api-6000-tps.js
```

## 🛠️ Customização

### Alterar TPS Target

Edite `person-api-6000-tps.js`:

```javascript
stages: [
  { duration: '1m', target: 1000 },
  { duration: '1m', target: 2000 },
  { duration: '1m', target: 3000 },
  { duration: '1m', target: 4000 },
  { duration: '1m', target: 5000 },
  { duration: '1m', target: 6000 },  // ← Altere aqui
  { duration: '30m', target: 6000 }, // ← E aqui
],
```

### Alterar Duração

```javascript
{ duration: '30m', target: 6000 },  // ← Altere aqui (30m = 30 minutos)
```

### Alterar Ramp-up

```javascript
// Ramp-up mais rápido (3 minutos)
{ duration: '30s', target: 2000 },
{ duration: '30s', target: 4000 },
{ duration: '1m', target: 6000 },

// Ramp-up mais lento (10 minutos)
{ duration: '2m', target: 1000 },
{ duration: '2m', target: 2000 },
// ...
```

### Alterar URL Base

```bash
# Via variável de ambiente
k6 run -e BASE_URL=http://192.168.1.100:8080 person-api-6000-tps.js

# Ou edite o arquivo:
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
```

## 📊 Integração com Grafana

### 1. Exportar para InfluxDB

```bash
k6 run --out influxdb=http://localhost:8086/k6 person-api-6000-tps.js
```

### 2. Configurar Grafana

1. Adicione InfluxDB como data source
2. Crie dashboard com queries:
   ```influxql
   SELECT mean("value") FROM "http_req_duration" WHERE time > now() - 1h GROUP BY time(10s)
   SELECT mean("value") FROM "http_reqs" WHERE time > now() - 1h GROUP BY time(10s)
   ```

## 🐛 Troubleshooting

### Erro: "Address already in use"
- **Causa**: Exaustão de portas efêmeras no Windows
- **Solução**: 
  ```powershell
  # Aumentar range de portas efêmeras
  netsh int ipv4 set dynamicport tcp start=10000 num=55000
  netsh int ipv4 set dynamicport udp start=10000 num=55000
  ```

### Erro: "Connection refused"
- **Causa**: API não está rodando
- **Solução**: Verificar se a API está em `http://localhost:8080`

### Thresholds falhando
- **Causa**: API não aguenta a carga
- **Solução**: 
  - Aumentar `server.tomcat.threads.max`
  - Aumentar `feign.httpclient.max-connections`
  - Reduzir TPS target no teste

### TPS não atinge 6000
- **Causa**: Limites de recursos (CPU, memória, rede)
- **Solução**:
  - Aumentar `maxVUs` no script
  - Verificar recursos do servidor
  - Executar K6 em máquina mais potente

### Muitos erros/timeouts
- **Causa**: API sobrecarregada
- **Solução**:
  - Reduzir TPS gradualmente
  - Verificar métricas do Tomcat (`tomcat.threads.busy`)
  - Verificar pool do Feign (`feign.httpclient.pool.leased`)

## 📚 Referências

- [K6 Documentation](https://k6.io/docs/)
- [K6 Scenarios](https://k6.io/docs/using-k6/scenarios/)
- [K6 Metrics](https://k6.io/docs/using-k6/metrics/)
- [K6 Thresholds](https://k6.io/docs/using-k6/thresholds/)
- [K6 Arrival Rate Executor](https://k6.io/docs/using-k6/scenarios/executors/ramping-arrival-rate/)

## 🆚 Comparação: K6 vs JMeter

| Característica | K6 | JMeter |
|---------------|-----|--------|
| **Linguagem** | JavaScript | XML/GUI |
| **Performance** | Alta (Go) | Média (Java) |
| **Open Model** | ✅ Nativo | ⚠️ Plugin |
| **CI/CD** | ✅ Excelente | ⚠️ Limitado |
| **Cloud** | ✅ K6 Cloud | ⚠️ JMeter Cloud |
| **Scripting** | ✅ Código | ⚠️ GUI/XML |
| **Métricas** | ✅ Ricas | ✅ Ricas |
| **Learning Curve** | ⚠️ Média | ✅ Baixa (GUI) |

## 💡 Dicas

1. **Execute em máquina dedicada**: K6 consome recursos, execute em máquina separada da API
2. **Monitore durante o teste**: Use Grafana/Prometheus para ver métricas em tempo real
3. **Comece com carga menor**: Use `person-api-1000-tps.js` antes de testar com 6000 TPS
4. **Use múltiplas saídas**: Exporte JSON + InfluxDB para análise completa
5. **Ajuste thresholds**: Ajuste os limites conforme seus SLAs

