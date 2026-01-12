package com.example.person.config;

import feign.Client;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Value("${feign.httpclient.max-connections:200}")
    private int maxConnections;

    @Value("${feign.httpclient.max-connections-per-route:50}")
    private int maxConnectionsPerRoute;

    @Value("${feign.httpclient.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${feign.httpclient.connection-request-timeout:3000}")
    private int connectionRequestTimeout;

    @Value("${feign.httpclient.socket-timeout:10000}")
    private int socketTimeout;

    @Value("${feign.httpclient.time-to-live:30000}")
    private int timeToLive;

    @Bean
    public PoolingHttpClientConnectionManager connectionManager() {
        // Configuração do Pool de Conexões para melhor throughput
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        // Número máximo de conexões totais no pool
        connectionManager.setMaxTotal(maxConnections);

        // Número máximo de conexões por rota (por host)
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);

        return connectionManager;
    }

    @Bean
    public Client feignClient(PoolingHttpClientConnectionManager connectionManager) {
        // Configuração de timeouts e outras opções
        RequestConfig requestConfig = RequestConfig.custom()
                // Timeout para estabelecer conexão (conectar ao servidor)
                .setConnectTimeout(connectionTimeout)
                // Timeout para obter conexão do pool
                .setConnectionRequestTimeout(connectionRequestTimeout)
                // Timeout para leitura de dados (socket timeout)
                .setSocketTimeout(socketTimeout)
                // Habilitar redirecionamento automático
                .setRedirectsEnabled(true)
                .setMaxRedirects(3)
                .build();

        // Construir o HttpClient com as configurações otimizadas
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                // Habilitar reuso de conexões (keep-alive) para melhor performance
                .setKeepAliveStrategy((response, context) -> timeToLive)
                .build();

        return new ApacheHttpClient(httpClient);
    }
}

