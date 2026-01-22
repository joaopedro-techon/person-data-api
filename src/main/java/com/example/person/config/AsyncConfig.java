package com.example.person.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Configuração do TaskExecutor para execução assíncrona de chamadas ao mock-api.
 * 
 * Otimizado para fazer múltiplas chamadas HTTP em paralelo de forma eficiente.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.executor.core-pool-size:50}")
    private int corePoolSize;

    @Value("${async.executor.max-pool-size:200}")
    private int maxPoolSize;

    @Value("${async.executor.queue-capacity:500}")
    private int queueCapacity;

    @Value("${async.executor.thread-name-prefix:async-external-api-}")
    private String threadNamePrefix;

    @Bean(name = "externalApiTaskExecutor")
    public Executor externalApiTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Tamanho do pool de threads
        executor.setCorePoolSize(corePoolSize);        // Threads sempre ativas
        executor.setMaxPoolSize(maxPoolSize);          // Máximo de threads
        executor.setQueueCapacity(queueCapacity);       // Capacidade da fila
        
        // Nome das threads para facilitar debugging
        executor.setThreadNamePrefix(threadNamePrefix);
        
        // Política de rejeição: CallerRunsPolicy executa na thread chamadora se o pool estiver cheio
        // Isso evita perda de tarefas e ajuda a controlar a carga
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        
        // Aguarda finalização de todas as tarefas ao desligar
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        return executor;
    }
}

