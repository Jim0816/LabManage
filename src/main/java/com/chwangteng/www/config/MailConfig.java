package com.chwangteng.www.config;


import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class MailConfig {
    private static final String PROPERTIES_DEFAULT = "mailConfig.properties";
    public static String host;
    public static Integer port;
    public static String userName;
    public static String passWord;
    public static String emailForm;
    public static String timeout;
    public static String personal;
    public static Properties properties;
    static{
        init();
    }

    /**
     * 初始化 TODO 先不读取配置
     */
    private static void init() {
        properties = new Properties();
        try{
            //InputStream inputStream = MailConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_DEFAULT);
            //properties.load(inputStream);
            //inputStream.close();
            //properties.setProperty("mailFrom","cuizhixiang@feitu.biz");
            //host = properties.getProperty("mailHost");
            //port = Integer.parseInt(properties.getProperty("mailPort"));
            //userName = properties.getProperty("mailUsername");
            //passWord = properties.getProperty("mailPassword");
            //emailForm = properties.getProperty("mailFrom");
            //timeout = properties.getProperty("mailTimeout");
            //personal = "墨裔";
            host = "smtp.qq.com";
            port = Integer.parseInt("465");
            userName = "1368606671@qq.com";
            passWord = "odmsiztkzpgghihc";
            emailForm = "1368606671@qq.com";
            timeout = "25000";
            personal = "周报管家";
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}


