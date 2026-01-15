import http from 'k6/http';
import { check } from 'k6';

// Teste rápido para validar configuração (100 TPS por 1 minuto)
export const options = {
  scenarios: {
    quick_test: {
      executor: 'ramping-arrival-rate',
      startRate: 0,
      timeUnit: '1s',
      preAllocatedVUs: 10,
      maxVUs: 200,
      stages: [
        { duration: '10s', target: 100 },  // Ramp-up rápido
        { duration: '20m', target: 100 },    // Mantém 100 TPS por 1 min
        { duration: '10s', target: 0 },     // Ramp-down
      ],
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<2000'],
    'http_req_failed': ['rate<0.05'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
  const personId = Math.floor(Math.random() * 1000) + 1;
  const response = http.get(`${BASE_URL}/api/person/${personId}`, {
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    },
    timeout: '30s',
  });

  check(response, {
    'status is 200': (r) => r.status === 200,
    'has person data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.id && body.name;
      } catch (e) {
        return false;
      }
    },
  });
}

