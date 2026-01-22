//package com.example.person.config;
//
//import io.micrometer.core.instrument.Gauge;
//import io.micrometer.core.instrument.MeterRegistry;
//import io.micrometer.core.instrument.binder.MeterBinder;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Configuração para expor métricas do Apache HttpClient Connection Pool
// * via Micrometer/Prometheus
// */
//@Configuration
//public class HttpClientMetricsConfig {
//
//    /**
//     * Registra métricas do pool de conexões HTTP
//     */
//    @Bean
//    public MeterBinder httpClientPoolMetrics(PoolingHttpClientConnectionManager connectionManager) {
//        return registry -> {
//            // Total de conexões configuradas no pool
//            Gauge.builder("httpclient.pool.max.total", connectionManager, cm -> cm.getTotalStats().getMax())
//                    .description("Número máximo de conexões totais no pool")
//                    .register(registry);
//
//            // Total de conexões disponíveis (livres)
//            Gauge.builder("httpclient.pool.available", connectionManager, cm -> cm.getTotalStats().getAvailable())
//                    .description("Número de conexões disponíveis (livres) no pool")
//                    .register(registry);
//
//            // Total de conexões em uso (leased)
//            Gauge.builder("httpclient.pool.leased", connectionManager, cm -> cm.getTotalStats().getLeased())
//                    .description("Número de conexões em uso (leased) no pool")
//                    .register(registry);
//
//            // Total de conexões pendentes (aguardando)
//            Gauge.builder("httpclient.pool.pending", connectionManager, cm -> cm.getTotalStats().getPending())
//                    .description("Número de requisições pendentes aguardando conexão")
//                    .register(registry);
//
//            // Taxa de utilização do pool (leased / max)
//            Gauge.builder("httpclient.pool.utilization.ratio", connectionManager, cm -> {
//                int max = cm.getTotalStats().getMax();
//                if (max == 0) return 0.0;
//                return (double) cm.getTotalStats().getLeased() / max;
//            })
//                    .description("Taxa de utilização do pool (0.0 a 1.0)")
//                    .register(registry);
//
//            // Taxa de utilização do pool em percentual
//            Gauge.builder("httpclient.pool.utilization.percent", connectionManager, cm -> {
//                int max = cm.getTotalStats().getMax();
//                if (max == 0) return 0.0;
//                return ((double) cm.getTotalStats().getLeased() / max) * 100.0;
//            })
//                    .description("Taxa de utilização do pool em percentual (0 a 100)")
//                    .register(registry);
//
//            // Total de conexões (available + leased)
//            Gauge.builder("httpclient.pool.total", connectionManager, cm ->
//                    cm.getTotalStats().getAvailable() + cm.getTotalStats().getLeased())
//                    .description("Total de conexões no pool (available + leased)")
//                    .register(registry);
//
//            // Máximo de conexões por rota
//            Gauge.builder("httpclient.pool.max.per.route", connectionManager, cm ->
//                    cm.getDefaultMaxPerRoute())
//                    .description("Número máximo de conexões por rota (por host)")
//                    .register(registry);
//        };
//    }
//}
//
