package com.chwangteng.www.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @description 定时任务线程池配置类
 * @return
 * @exception
 * @author Jim
 * @date 2022/4/16 14:05
 **/
@Slf4j
@Configuration
public class MyTaskSchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        log.info("创建定时任务调度线程池 start");
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(20);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        log.info("创建定时任务调度线程池 end");
        return executor;
    }
}

