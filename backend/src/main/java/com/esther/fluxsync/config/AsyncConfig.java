package com.esther.fluxsync.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfig {

    // 通用的线程池执行器
    // core threads 10
    // max threads 50
    // queue size 100
    // threads prefix "async-"
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);   // 设置核心线程数
        executor.setMaxPoolSize(50);    // 设置最大线程数
        executor.setQueueCapacity(100); // 设置队列容量
        executor.setThreadNamePrefix("async-"); // 设置线程名的前缀
        executor.initialize();
        return executor;
    }

    // 任务调度器
    // tasks pool size 10
    // tasks prefix "task-"
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("task-");
        scheduler.initialize();
        return scheduler;
    }

}
