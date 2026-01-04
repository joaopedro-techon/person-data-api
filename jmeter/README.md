# Teste de Carga - Person API

Este diretório contém o arquivo JMX para executar testes de carga na Person API usando Apache JMeter.

## Arquivo

- `person-api-load-test.jmx` - Script JMeter configurado para 500 requisições por segundo

## Configuração do Teste

- **Target RPS**: 500 requisições por segundo
- **Threads**: 100 threads
- **Ramp-up**: 60 segundos (tempo para atingir 100 threads)
- **Duração**: 300 segundos (5 minutos)
- **Endpoint**: GET `/api/person/{id}` onde `{id}` varia aleatoriamente entre 1 e 1000
- **Assertion**: Valida que o código de resposta é 200

## Pré-requisitos

1. Apache JMeter instalado (versão 5.6 ou superior)
2. Aplicação Spring Boot rodando em `http://localhost:8080`
3. WireMock rodando em `http://localhost:8089`

## Como Executar

### Opção 1: Interface Gráfica do JMeter

1. Abra o JMeter
2. File → Open → Selecione `person-api-load-test.jmx`
3. Clique em Run → Start (ou pressione Ctrl+R)
4. Monitore os resultados nos listeners:
   - **Summary Report**: Estatísticas resumidas
   - **Aggregate Report**: Relatório agregado com médias, mínimos, máximos
   - **Graph Results**: Gráfico de tempo de resposta ao longo do tempo
   - **View Results Tree**: Detalhes de cada requisição (desabilite em produção para melhor performance)

### Opção 2: Linha de Comando (Non-GUI Mode)

Execute o teste via linha de comando para melhor performance:

```bash
# Windows
jmeter -n -t person-api-load-test.jmx -l results.jtl -e -o report/

# Linux/Mac
./jmeter -n -t person-api-load-test.jmx -l results.jtl -e -o report/
```

Parâmetros:
- `-n`: Modo não-GUI
- `-t`: Arquivo de teste JMX
- `-l`: Arquivo de log de resultados (JTL)
- `-e`: Gerar relatório HTML após o teste
- `-o`: Diretório de saída do relatório HTML

## Monitoramento Durante o Teste

Enquanto o teste está rodando, você pode monitorar:

1. **Prometheus**: http://localhost:9090
   - Verifique as métricas de latência, throughput e erros
   - Query: `rate(person_endpoint_requests_total[1m])` para ver requisições por segundo

2. **Grafana**: http://localhost:3000
   - Dashboard com gráficos de threads, latência, erros, etc.

3. **Actuator Health**: http://localhost:8080/actuator/health
   - Verifique o status da aplicação

## Métricas Importantes a Observar

- **Throughput**: Deve estar próximo de 500 requisições/segundo
- **Response Time**: Tempo médio, P95, P99 de resposta
- **Error Rate**: Taxa de erros (deve ser baixa)
- **Thread Count**: Número de threads JVM (pode aumentar sob carga)
- **CPU e Memória**: Uso de recursos do servidor

## Ajustando a Carga

Para modificar a taxa de requisições por segundo:

1. Abra o arquivo JMX no JMeter
2. Localize o elemento "Constant Throughput Timer - 500 RPS"
3. Altere o valor de `throughput` (atualmente 500.0)
4. Salve o arquivo

Para ajustar o número de threads ou duração:

1. Localize o "Thread Group - 500 RPS"
2. Modifique:
   - `num_threads`: Número de threads simultâneas
   - `ramp_time`: Tempo de ramp-up em segundos
   - `duration`: Duração total do teste em segundos

## Dicas

- **Para testes mais longos**: Aumente a duração no Thread Group
- **Para mais carga**: Aumente o número de threads e o throughput
- **Para melhor performance no JMeter**: Desabilite listeners desnecessários (View Results Tree) durante execução
- **Para análise posterior**: Use o modo não-GUI e gere relatórios HTML

