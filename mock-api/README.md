# Mock External API

API mock de alta performance desenvolvida em Spring Boot para simular uma API externa com latência de 500ms e suporte a 5000+ TPS.

## ⚠️ IMPORTANTE: Problemas com Docker?

Se você está tendo problemas de proxy/rede ou imagens não encontradas no Docker, **RODE LOCALMENTE** - é muito mais simples!

```bash
# Windows
cd mock-api
.\run-local.bat

# Linux/Mac  
cd mock-api
./run-local.sh
```

Veja `INICIO-RAPIDO.md` na raiz do projeto para mais detalhes.

## Características

- **Latência fixa**: 500ms por requisição
- **Alta performance**: Configurado para suportar 5000+ TPS
- **Thread pool otimizado**: 1000 threads máximas
- **Conexões simultâneas**: Até 10.000 conexões
- **Compressão HTTP**: Habilitada para reduzir tamanho das respostas

## Endpoints

### GET /external-person/{id}

Retorna dados de uma pessoa simulada com latência de 500ms.

**Exemplo de requisição:**
```bash
curl http://localhost:8089/external-person/1
```

**Exemplo de resposta:**
```json
{
  "id": 1,
  "name": "João Silva",
  "age": 30,
  "email": "joao.silva@example.com"
}
```

### GET /actuator/health

Health check endpoint.

## Como executar

### Opção 1: Localmente com Maven (RECOMENDADO se Docker tiver problemas)

**Windows:**
```bash
cd mock-api
run-local.bat
```

**Linux/Mac:**
```bash
cd mock-api
chmod +x run-local.sh
./run-local.sh
```

**Ou manualmente:**
```bash
cd mock-api
mvn spring-boot:run
```

### Opção 2: Via Docker Compose

```bash
# Na raiz do projeto
docker-compose up -d mock-api
```

**Se tiver problemas de conexão com Docker Hub:**
1. Verifique sua conexão de internet
2. Configure proxy do Docker se necessário
3. Tente usar o Dockerfile alternativo:
   ```bash
   docker build -f mock-api/Dockerfile.alternative -t mock-api ./mock-api
   ```
4. Ou use o Dockerfile mais simples:
   ```bash
   docker build -f mock-api/Dockerfile.simple -t mock-api ./mock-api
   ```

### Opção 3: Build manual e executar JAR

```bash
cd mock-api
mvn clean package
java -Xms512m -Xmx2g -XX:+UseG1GC -jar target/mock-external-api-0.0.1-SNAPSHOT.jar
```

## Solução de Problemas

### Erro ao construir Docker

Se receber erro de conexão com Docker Hub:
- **Solução 1**: Rode localmente (Opção 1 acima) - mais simples e rápido
- **Solução 2**: Configure proxy do Docker:
  ```json
  {
    "proxies": {
      "default": {
        "httpProxy": "http://proxy.example.com:8080",
        "httpsProxy": "http://proxy.example.com:8080"
      }
    }
  }
  ```
- **Solução 3**: Use imagens já baixadas localmente

## Configurações de Performance

As configurações estão em `src/main/resources/application.properties`:

- `server.tomcat.threads.max=1000`: Máximo de threads
- `server.tomcat.max-connections=10000`: Máximo de conexões simultâneas
- `server.tomcat.accept-count=2000`: Tamanho da fila de requisições pendentes

## Ajustando a Latência

Para alterar a latência, edite o arquivo `MockPersonController.java`:

```java
private static final int FIXED_LATENCY_MS = 500; // Altere este valor
```

## Monitoramento

A API expõe endpoints do Actuator:
- `/actuator/health`: Health check
- `/actuator/info`: Informações da aplicação

