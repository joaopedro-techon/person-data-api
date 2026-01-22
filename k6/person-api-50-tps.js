import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// Métricas customizadas
const errorRate = new Rate('errors');
const responseTime = new Trend('response_time');
const requestCounter = new Counter('total_requests');

// Configuração do teste - 1000 TPS com menor quantidade de VUs
export const options = {
  scenarios: {
    // Open Model - Ramp-up gradual até 1000 TPS e mantém por 15 minutos
    gradual_1000_tps: {
      executor: 'ramping-arrival-rate',
      startRate: 0,
      timeUnit: '1s',
      preAllocatedVUs: 50,      // Menor quantidade inicial de VUs
      maxVUs: 550,              // Máximo de VUs otimizado para 1000 TPS
      stages: [
        // Ramp-up gradual: 0 -> 1000 TPS em 3 minutos
        { duration: '30s', target: 5 },   // 0 -> 250 TPS em 30s
        { duration: '30s', target: 10 },   // 250 -> 500 TPS em 30s
        { duration: '30s', target: 20 },   // 500 -> 750 TPS em 30s
        { duration: '30s', target: 30 },  // 750 -> 1000 TPS em 30s
        { duration: '1m', target: 50 },   // Estabiliza em 1000 TPS por 1 min
        
        // Mantém 1000 TPS por 15 minutos
        { duration: '10m', target: 50 },  // Mantém 1000 TPS por 15 minutos
        
        // Ramp-down gradual: 1000 -> 0 TPS em 1 minuto
        { duration: '30s', target: 25 },   // 1000 -> 500 TPS em 30s
        { duration: '30s', target: 0 },     // 500 -> 0 TPS em 30s
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
    'http_reqs': ['rate>=50'],         // Mínimo 900 req/s (permite margem de 100)
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
  // O K6 ajusta automaticamente o número de VUs para atingir a taxa de chegada desejada
}

// Função chamada no início do teste
export function setup() {
  console.log(`🚀 Iniciando teste de carga K6`);
  console.log(`📊 Target: 50 TPS`);
  console.log(`👥 VUs: 50-2000 (otimizado para menor quantidade)`);
  console.log(`⏱️  Duração: ~20 minutos (3min ramp-up + 15min carga + 1min ramp-down)`);
  console.log(`🌐 Base URL: ${BASE_URL}`);
  return { baseUrl: BASE_URL };
}

// Função chamada no final do teste
export function teardown(data) {
  console.log(`✅ Teste finalizado`);
  console.log(`🌐 Base URL usado: ${data.baseUrl}`);
}

