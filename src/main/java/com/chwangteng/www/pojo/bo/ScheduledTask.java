package com.chwangteng.www.pojo.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ScheduledTask
 * @Description
 * @Author Jim
 * @Date 2022/4/16 14:07
 **/

@Data
public class ScheduledTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private String id;

    /** 定时任务名称 */
    private String taskName;

    /** 定时任务完整类名 */
    private String taskClass;

    /** cron表达式 */
    private String cronExpression;

    /** 任务描述 */
    private String taskExplain;

    /** 状态：1.启用；2.停用 */
    private int status;

    /** 创建人. */
    private String createBy;

    /** 创建时间. */
    private Date createTime;

    /** 修改人. */
    private String updateBy;

    /** 修改时间. */
    private Date updateTime;
}
