# Mock API - Endpoints Disponíveis

Esta API mock simula uma API externa com latência fixa de **500ms** em todos os endpoints.

## Base URL

```
http://localhost:8089
```

## Endpoints

### 1. Buscar Dados Básicos do Cliente

**GET** `/external-person/{id}`

Retorna dados básicos do cliente (nome, idade, email).

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "name": "João Silva",
  "age": 30,
  "email": "joao.silva@example.com"
}
```

---

### 2. Buscar Dados do Endereço

**GET** `/external-person/{id}/address`

Retorna dados completos do endereço do cliente.

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "street": "Rua das Flores",
  "number": "123",
  "complement": "Apto 45",
  "neighborhood": "Centro",
  "city": "São Paulo",
  "state": "SP",
  "zipCode": "01310-100",
  "country": "Brasil"
}
```

---

### 3. Buscar Data de Nascimento

**GET** `/external-person/{id}/birth-date`

Retorna dados referentes à data de nascimento do cliente.

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "birthDate": "1993-05-15",
  "age": 30,
  "zodiacSign": "Touro"
}
```

---

### 4. Buscar Nomes dos Pais

**GET** `/external-person/{id}/parents`

Retorna os nomes do pai e da mãe do cliente.

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "fatherName": "Carlos Silva",
  "motherName": "Maria Silva"
}
```

---

### 5. Buscar Dados de Telefone

**GET** `/external-person/{id}/phone`

Retorna todos os telefones cadastrados do cliente (móvel, residencial, trabalho).

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "phones": [
    {
      "type": "MOBILE",
      "number": "11987654321",
      "countryCode": "+55"
    },
    {
      "type": "HOME",
      "number": "1133334444",
      "countryCode": "+55"
    },
    {
      "type": "WORK",
      "number": "1122223333",
      "countryCode": "+55"
    }
  ]
}
```

---

### 6. Buscar Dados de Escolaridade

**GET** `/external-person/{id}/education`

Retorna dados de escolaridade e formação do cliente.

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "educationLevel": "GRADUATE",
  "institution": "Universidade de São Paulo",
  "course": "Ciência da Computação",
  "graduationYear": 2015,
  "isCompleted": true
}
```

**Níveis de Escolaridade:**
- `ELEMENTARY` - Ensino Fundamental
- `HIGH_SCHOOL` - Ensino Médio
- `UNDERGRADUATE` - Graduação
- `GRADUATE` - Pós-graduação
- `POSTGRADUATE` - Pós-graduação stricto sensu

---

### 7. Buscar Cidade de Nascimento

**GET** `/external-person/{id}/birth-city`

Retorna a cidade, estado, país e coordenadas geográficas do local de nascimento.

**Exemplo de Resposta:**
```json
{
  "id": 1,
  "birthCity": "São Paulo",
  "birthState": "SP",
  "birthCountry": "Brasil",
  "coordinates": {
    "latitude": -23.5505,
    "longitude": -46.6333
  }
}
```

---

## Características

- **Latência Fixa**: Todos os endpoints têm latência de **500ms**
- **Alta Performance**: Configurado para suportar alta concorrência
- **Thread Pool**: 2000 threads máximas
- **Conexões**: Até 20000 conexões simultâneas

## Exemplos de Uso

### cURL

```bash
# Dados básicos
curl http://localhost:8089/external-person/1

# Endereço
curl http://localhost:8089/external-person/1/address

# Data de nascimento
curl http://localhost:8089/external-person/1/birth-date

# Pais
curl http://localhost:8089/external-person/1/parents

# Telefone
curl http://localhost:8089/external-person/1/phone

# Escolaridade
curl http://localhost:8089/external-person/1/education

# Cidade de nascimento
curl http://localhost:8089/external-person/1/birth-city
```

### JavaScript (Fetch)

```javascript
const id = 1;

// Buscar endereço
const addressResponse = await fetch(`http://localhost:8089/external-person/${id}/address`);
const address = await addressResponse.json();

// Buscar telefone
const phoneResponse = await fetch(`http://localhost:8089/external-person/${id}/phone`);
const phone = await phoneResponse.json();
```

### Java (Feign Client)

```java
@FeignClient(name = "external-person-api", url = "http://localhost:8089")
public interface ExternalPersonClient {
    
    @GetMapping("/external-person/{id}/address")
    AddressResponse getAddress(@PathVariable Long id);
    
    @GetMapping("/external-person/{id}/phone")
    PhoneResponse getPhone(@PathVariable Long id);
    
    // ... outros endpoints
}
```

## Health Check

**GET** `/actuator/health`

Verifica se a API está funcionando.

```json
{
  "status": "UP"
}
```

