package com.chwangteng.www.Utils;

import com.chwangteng.www.pojo.bo.TaskScheduleModel;

/**
 * @ClassName: CronUtil
 * @Description: Cron表达式工具类
 * 目前支持三种常用的cron表达式
 * 1.每天的某个时间点执行 例:12 12 12 * * ?表示每天12时12分12秒执行
 * 2.每周的哪几天执行         例:12 12 12 ? * 1,2,3表示每周的周1周2周3 ,12时12分12秒执行
 * 3.每月的哪几天执行         例:12 12 12 1,21,13 * ?表示每月的1号21号13号 12时12分12秒执行
 * @author
 * @date
 *
 */
public class CronUtil {

    /**
     *
     *方法摘要：构建Cron表达式
     *@param  taskScheduleModel
     *@return String
     */
    public static String createCronExpression(TaskScheduleModel taskScheduleModel){
        StringBuffer cronExp = new StringBuffer("");

        if(null == taskScheduleModel.getJobType()) {
            System.out.println("执行周期未配置" );//执行周期未配置
        }

        if (null != taskScheduleModel.getSecond()
                && null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //秒
            cronExp.append(taskScheduleModel.getSecond()).append(" ");
            //分
            cronExp.append(taskScheduleModel.getMinute()).append(" ");
            //小时
            cronExp.append(taskScheduleModel.getHour()).append(" ");

            //每天
            if(taskScheduleModel.getJobType().intValue() == 1){
                cronExp.append("* ");//日
                cronExp.append("* ");//月
                cronExp.append("?");//周
            }

            //按每周
            else if(taskScheduleModel.getJobType().intValue() == 3){
                //一个月中第几天
                cronExp.append("? ");
                //月份
                cronExp.append("* ");
                //周
                Integer[] weeks = taskScheduleModel.getDayOfWeeks();
                for(int i = 0; i < weeks.length; i++){
                    if(i == 0){
                        cronExp.append(weeks[i]);
                    } else{
                        cronExp.append(",").append(weeks[i]);
                    }
                }

            }

            //按每月
            else if(taskScheduleModel.getJobType().intValue() == 2){
                //一个月中的哪几天
                Integer[] days = taskScheduleModel.getDayOfMonths();
                for(int i = 0; i < days.length; i++){
                    if(i == 0){
                        cronExp.append(days[i]);
                    } else{
                        cronExp.append(",").append(days[i]);
                    }
                }
                //月份
                cronExp.append(" * ");
                //周
                cronExp.append("?");
            }

        }
        else {
            System.out.println("时或分或秒参数未配置" );//时或分或秒参数未配置
        }
        return cronExp.toString();
    }

    /**
     *
     *方法摘要：生成计划的详细描述
     *@param  taskScheduleModel
     *@return String
     */
    public static String createDescription(TaskScheduleModel taskScheduleModel){
        StringBuffer description = new StringBuffer("");
        //计划执行开始时间
//      Date startTime = taskScheduleModel.getScheduleStartTime();

        if (null != taskScheduleModel.getSecond()
                && null != taskScheduleModel.getMinute()
                && null != taskScheduleModel.getHour()) {
            //按每天
            if(taskScheduleModel.getJobType().intValue() == 1){
                description.append("每天");
                description.append(taskScheduleModel.getHour()).append("时");
                description.append(taskScheduleModel.getMinute()).append("分");
                description.append(taskScheduleModel.getSecond()).append("秒");
                description.append("执行");
            }

            //按每周
            else if(taskScheduleModel.getJobType().intValue() == 3){
                if(taskScheduleModel.getDayOfWeeks() != null && taskScheduleModel.getDayOfWeeks().length > 0) {
                    String days = "";
                    for(int i : taskScheduleModel.getDayOfWeeks()) {
                        days += "周" + i;
                    }
                    description.append("每周的").append(days).append(" ");
                }
                if (null != taskScheduleModel.getSecond()
                        && null != taskScheduleModel.getMinute()
                        && null != taskScheduleModel.getHour()) {
                    description.append(",");
                    description.append(taskScheduleModel.getHour()).append("时");
                    description.append(taskScheduleModel.getMinute()).append("分");
                    description.append(taskScheduleModel.getSecond()).append("秒");
                }
                description.append("执行");
            }

            //按每月
            else if(taskScheduleModel.getJobType().intValue() == 2){
                //选择月份
                if(taskScheduleModel.getDayOfMonths() != null && taskScheduleModel.getDayOfMonths().length > 0) {
                    String days = "";
                    for(int i : taskScheduleModel.getDayOfMonths()) {
                        days += i + "号";
                    }
                    description.append("每月的").append(days).append(" ");
                }
                description.append(taskScheduleModel.getHour()).append("时");
                description.append(taskScheduleModel.getMinute()).append("分");
                description.append(taskScheduleModel.getSecond()).append("秒");
                description.append("执行");
            }

        }
        return description.toString();
    }

    /**
     * @description 获取每周具体哪一天的几点执行 cron表达式  0 0 14 ? * 7
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/16 9:55
     **/
    public static String getCronStr(int weekDay, String time){
        String cronPattern = "0 {minute} {hour} ? * {week}";
        String times[] = time.split(":");
        int hour = Integer.valueOf(times[0]);
        int minute = Integer.valueOf(times[1]);

        return cronPattern.replaceAll("\\{week\\}", String.valueOf(weekDay))
                .replaceAll("\\{hour\\}", String.valueOf(hour))
                .replaceAll("\\{minute\\}", String.valueOf(minute));
    }

    /**
     * @description 解析出cron时间
     * @return
     * @exception
     * @author Jim
     * @date 2022/4/16 12:39
     **/
    public static String[] reverseCron(String cron){
        String a[] = cron.split(" ");
        String minute = Integer.valueOf(a[1]) == 0 ? "00" : Integer.valueOf(a[1]).toString();
        String hour = Integer.valueOf(a[2]) == 0 ? "00" : Integer.valueOf(a[2]).toString();
        int week = Integer.valueOf(a[a.length-1]);

        String time = hour + ":" + minute;
        return new String[]{String.valueOf(week), time};
    }

    //参考例子
    public static void main(String[] args) {
        String cron = getCronStr(6, "23:00");
        System.out.println(cron);
        String[] a = reverseCron(cron);
        System.out.println(a);
    }
}
