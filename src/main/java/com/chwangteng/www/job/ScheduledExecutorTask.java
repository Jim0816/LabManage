package com.chwangteng.www.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

/**
 * Spring动态周期定时任务<br>
 * 在不停应用的情况下更改任务执行周期
 */
@Lazy(false)
@Component
@EnableScheduling
public class ScheduledExecutorTask {

    @Autowired
    ThreadPoolTaskScheduler scheduler;

    private ScheduledFuture<?> future;
    private static String cron = "0 0 0 ? * 7"; //默认每周日晚上24点
    private static int status = 0;              //0 开启  1 关闭

    public void setTimingDate(String cronStr) {
        cron = cronStr == null ? cron : cronStr;
        if (future != null){
            future.cancel(true);
        }
        start();
    }

    public void setIs_open(int is_open) {
        status = is_open;
    }

    private void start() {
        future = scheduler.schedule(() -> {
            //TODO 业务逻辑
            //查看所有没有查询
            System.out.println("========================定时调度==========================");
        }, (triggerContext) -> {
            CronTrigger trigger = new CronTrigger(cron);
            return trigger.nextExecutionTime(triggerContext);
        });
    }


}

