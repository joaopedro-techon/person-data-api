import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// Métricas customizadas
const errorRate = new Rate('errors');
const responseTime = new Trend('response_time');
const requestCounter = new Counter('total_requests');

// Configuração do teste
export const options = {
  scenarios: {
    // Open Model - Ramp-up gradual até 6000 TPS e mantém por 30 minutos
    gradual_6000_tps: {
      executor: 'ramping-arrival-rate',
      startRate: 0,
      timeUnit: '1s',
      preAllocatedVUs: 100,
      maxVUs: 10000, // Máximo de VUs para suportar 6000 TPS
      stages: [
        // Ramp-up gradual: 0 -> 6000 TPS em 5 minutos
        { duration: '1m', target: 1000 },   // 0 -> 1000 TPS em 1 min
        { duration: '1m', target: 2000 },   // 1000 -> 2000 TPS em 1 min
        { duration: '1m', target: 3000 },   // 2000 -> 3000 TPS em 1 min
        { duration: '1m', target: 4000 },   // 3000 -> 4000 TPS em 1 min
        { duration: '1m', target: 5000 },   // 4000 -> 5000 TPS em 1 min
        { duration: '1m', target: 6000 },   // 5000 -> 6000 TPS em 1 min
        
        // Mantém 6000 TPS por 30 minutos
        { duration: '30m', target: 6000 },  // Mantém 6000 TPS por 30 minutos
        
        // Ramp-down gradual: 6000 -> 0 TPS em 2 minutos
        { duration: '1m', target: 3000 },   // 6000 -> 3000 TPS em 1 min
        { duration: '1m', target: 0 },      // 3000 -> 0 TPS em 1 min
      ],
    },
  },
  thresholds: {
    // Tempo de resposta
    'http_req_duration': [
      'p(50)<500',   // 50% das requisições < 500ms
      'p(75)<1000',  // 75% das requisições < 1s
      'p(95)<2000',  // 95% das requisições < 2s
      'p(99)<5000',  // 99% das requisições < 5s
    ],
    // Taxa de erros
    'http_req_failed': ['rate<0.05'],  // < 5% de erros HTTP
    'errors': ['rate<0.05'],            // < 5% de erros (checks)
    // Throughput
    'http_reqs': ['rate>=5000'],        // Mínimo 5000 req/s (permite margem)
  },
};

// Configurações
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ENDPOINT = '/api/person';

export default function () {
  // Gera ID aleatório entre 1 e 1000
  const personId = Math.floor(Math.random() * 1000) + 1;
  const url = `${BASE_URL}${ENDPOINT}/${personId}`;

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    timeout: '30s',
    tags: {
      name: 'GetPersonById',
      endpoint: 'person',
    },
  };

  const startTime = Date.now();
  const response = http.get(url, params);
  const duration = Date.now() - startTime;

  // Incrementa contador de requisições
  requestCounter.add(1);

  // Verifica resposta
  const success = check(response, {
    'status is 200': (r) => r.status === 200,
    'response time < 5s': (r) => r.timings.duration < 5000,
    'has person data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.id && body.name && body.email;
      } catch (e) {
        return false;
      }
    },
    'content type is json': (r) => r.headers['Content-Type'] && r.headers['Content-Type'].includes('application/json'),
  });

  // Registra métricas
  errorRate.add(!success);
  responseTime.add(duration);

  // Em open model (arrivals), não precisa sleep - o executor controla o ritmo
  // Mas podemos adicionar um pequeno sleep se necessário para simular comportamento real
  // sleep(0.1); // 100ms entre requisições (opcional)
}

// Função chamada no início do teste
export function setup() {
  console.log(`🚀 Iniciando teste de carga K6`);
  console.log(`📊 Target: 6000 TPS`);
  console.log(`⏱️  Duração: ~38 minutos (6min ramp-up + 30min carga + 2min ramp-down)`);
  console.log(`🌐 Base URL: ${BASE_URL}`);
  return { baseUrl: BASE_URL };
}

// Função chamada no final do teste
export function teardown(data) {
  console.log(`✅ Teste finalizado`);
  console.log(`🌐 Base URL usado: ${data.baseUrl}`);
}

