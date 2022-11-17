package com.shankhadeepghoshal.learnkafka.taxiprocessor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadingConfig {
  @Bean
  public TaskExecutor threadPoolTaskExecutor(
      @Value("${kafka.topic.partition}") final Integer numPartitions) {
    final var numThreads = Math.min(numPartitions, Runtime.getRuntime().availableProcessors());
    final var executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(numThreads);
    executor.setMaxPoolSize(numThreads);
    executor.setThreadNamePrefix("kafka_dedicated_pool");
    executor.initialize();

    return executor;
  }
}
