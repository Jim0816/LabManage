/*
package com.chwangteng.www.service.impl;

import com.chwangteng.www.pojo.bo.ScheduledTask;
import com.chwangteng.www.service.ScheduledTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

*/
/**
 * @ClassName ScheduledTaskServiceImpl
 * @Description
 * @Author Jim
 * @Date 2022/4/16 14:11
 **//*

@Slf4j
@Service("scheduledTaskService")
public class ScheduledTaskServiceImpl implements ScheduledTaskService {
    */
/** 定时任务线程池 *//*

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    */
/** 处于启动状态 的 定时任务集合 *//*

    public Map<String, ScheduledFuture> scheduledFutureMap =
            new ConcurrentHashMap<String, ScheduledFuture>();

    public boolean start(String id) {
        // 根据id查询任务
        ScheduledTask task = super.getById(id);
        // 判断任务是否启用
        if (1 != task.getStatus()) {
            return Result.error("定时任务未启用，无法执行！");
        }
        String taskClass = task.getTaskClass();
        log.info("启动定时任务：" + taskClass);
        // 添加锁放一个线程启动，防止多人启动多次
        lock.lock();
        log.info("加锁完成");
        try {
            if (this.isStart(id)) {
                String msg = "当前任务在启动状态中";
                log.info(msg);
                return Result.error(msg);
            }
            // 任务启动
            this.doStartTask(task);
        } finally {
            lock.unlock();
            log.info("解锁完毕");
        }
        return Result.OK();
    }

}
*/
