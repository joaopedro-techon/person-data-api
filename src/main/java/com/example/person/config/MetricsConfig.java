package com.example.person.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> {
            // Métricas customizadas de threads
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            
            // Gauge para threads por estado
            registry.gauge("jvm.threads.count", Tags.of("state", "runnable"), 
                threadBean, ThreadMXBean::getThreadCount);
            
            // Gauge para threads disponíveis (diferentes estados)
            registry.gauge("jvm.threads.peak", threadBean, ThreadMXBean::getPeakThreadCount);
            registry.gauge("jvm.threads.daemon", threadBean, ThreadMXBean::getDaemonThreadCount);
        };
    }
}

