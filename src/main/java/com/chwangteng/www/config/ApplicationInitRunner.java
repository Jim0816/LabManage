package com.chwangteng.www.config;

/**
 * @ClassName ApplicationInitRunner
 * @Description
 * @Author Jim
 * @Date 2022/4/16 13:45
 **/

import com.chwangteng.www.job.ScheduledExecutorTask;
import com.chwangteng.www.mapper.TeacherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 用于启动服务时加载执行动作
 * @author Jim
 */
//@Component
public class ApplicationInitRunner implements ApplicationListener<ContextRefreshedEvent> {
    //@Autowired
    private ScheduledExecutorTask scheduledExecutorTask;

    //@Autowired
    private TeacherMapper teacherMapper;

    //@Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("=================启动时执行===============");
        //Teacher teacher = teacherMapper.selectByPrimaryKey(userid);
        scheduledExecutorTask.setTimingDate(null);
    }
}
