package com.chwangteng.www.pojo.consts;

/**
 * @ClassName DocUtil
 * @Description
 * @Author Gcy
 * @Date 2022/5/14 22:31
 **/
public class ReportConfig {
    public static String reportRootPath = ""; //周报保存服务器位置
    public static String zipPath = "";  //压缩文件所在位置
    public static String path = "download/report.zip"; // 返回前端下载路径

    public static void initReport(String path){
        reportRootPath = path + "/download/report/";
        zipPath = path + "/download/report.zip";
    }

}