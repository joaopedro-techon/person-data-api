# Tail-Based Sampling com OpenTelemetry Collector

O OpenTelemetry Collector está configurado com **tail-based sampling** para reduzir o volume de traces enviados ao Jaeger.

## 🎯 Política de Sampling

O Collector envia traces para o Jaeger **apenas** se:

1. ✅ **Latência > 300ms** - Requests lentos são sempre rastreados
2. ✅ **Erro** - Qualquer erro (status ERROR) é sempre rastreado

**Traces com latência ≤ 300ms e sem erros são descartados.**

## 📊 Como Funciona

```
Aplicação (person-api / mock-api)
    ↓ Envia TODOS os traces
OpenTelemetry Collector (porta 4317)
    ↓ Tail-Based Sampling
    ├── Latência > 300ms? → ✅ Envia para Jaeger
    ├── Erro? → ✅ Envia para Jaeger
    └── Latência ≤ 300ms e sem erro? → ❌ Descarta
    ↓
Jaeger (porta 4317)
    ↓
Jaeger UI (porta 16686)
```

## 🚀 Como Usar

### 1. Iniciar o Collector e Jaeger

```bash
docker-compose up -d otel-collector jaeger
```

### 2. Verificar se está rodando

```bash
docker-compose ps otel-collector jaeger
```

### 3. Executar as aplicações

**Person API:**
```bash
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

**Mock API (local):**
```bash
cd mock-api
.\run-with-otel.bat  # Windows
# ou
./run-with-otel.sh   # Linux/Mac
```

**Mock API (Docker):**
```bash
docker-compose up -d mock-api
```

### 4. Fazer Requisições

```bash
# Requisição normal (latência ~500ms do mock-api)
# Será enviada ao Jaeger porque latência > 300ms
curl http://localhost:8080/api/person/1

# Requisição rápida (se houver cache ou resposta rápida)
# NÃO será enviada ao Jaeger se latência ≤ 300ms
curl http://localhost:8080/api/person/2
```

## 🔍 Verificar no Jaeger

1. Acesse: http://localhost:16686
2. Selecione o serviço (`person-api` ou `mock-api`)
3. Clique em "Find Traces"
4. Você verá **apenas** traces com:
   - Latência > 300ms
   - Ou erros

## ⚙️ Configuração

### Arquivo: `otel-collector/otel-collector-config.yaml`

```yaml
processors:
  tail_sampling:
    decision_wait: 10s          # Aguarda 10s para coletar spans
    num_traces: 20000           # Máximo de traces em memória
    expected_new_traces_per_sec: 1000
    
    policies:
      # Sempre coletar erros
      - name: errors-only
        type: status_code
        status_code:
          status_codes: [ERROR]
      
      # Coletar requests com latência > 300ms
      - name: slow-requests
        type: latency
        latency:
          threshold_ms: 300
```

### Ajustar Threshold

Para mudar o threshold de latência, edite `otel-collector-config.yaml`:

```yaml
latency:
  threshold_ms: 500  # Mudar para 500ms, por exemplo
```

Depois, reinicie o Collector:

```bash
docker-compose restart otel-collector
```

## 📈 Benefícios

1. **Redução de Volume**: Apenas traces relevantes são armazenados
2. **Foco em Problemas**: Erros e lentidão são sempre rastreados
3. **Economia de Recursos**: Menos dados no Jaeger = melhor performance
4. **Custo Reduzido**: Se usar Jaeger Cloud ou storage pago, reduz custos

## 🐛 Troubleshooting

### Nenhum trace aparece no Jaeger

**Causa**: Todos os traces têm latência ≤ 300ms e sem erros.

**Solução**: 
- Faça uma requisição que demore mais de 300ms
- Ou force um erro (ex: ID inválido que retorna 404/500)

### Verificar logs do Collector

```bash
docker-compose logs -f otel-collector
```

Você deve ver logs indicando traces sendo processados e enviados.

### Ajustar decision_wait

Se traces estão sendo perdidos, aumente o `decision_wait`:

```yaml
tail_sampling:
  decision_wait: 30s  # Aumentar de 10s para 30s
```

Isso dá mais tempo para coletar todos os spans de um trace antes de decidir.

## 📝 Notas Importantes

- **Tail-Based Sampling**: A decisão é feita **após** ver o trace completo
- **Latência Total**: O Collector mede a latência total do trace (do primeiro ao último span)
- **Erros**: Qualquer span com status ERROR faz o trace inteiro ser enviado
- **Memória**: O Collector mantém traces em memória até decidir (configurado em `num_traces`)

