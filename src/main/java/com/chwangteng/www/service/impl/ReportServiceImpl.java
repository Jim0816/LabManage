package com.chwangteng.www.service.impl;

import com.chwangteng.www.mapper.ReportMapper;
import com.chwangteng.www.mapper.StudentMapper;
import com.chwangteng.www.mapper.TeacherMapper;
import com.chwangteng.www.param.ViewPeersReportParam;
import com.chwangteng.www.param.ViewStudentsReportParam;
import com.chwangteng.www.pojo.ReportExample;
import com.chwangteng.www.pojo.ReportWithBLOBs;
import com.chwangteng.www.pojo.Student;
import com.chwangteng.www.pojo.StudentExample;
import com.chwangteng.www.service.ReportService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    public List getPeersReport(int id, ViewPeersReportParam viewPeersReportParam) {

/*        int pageIndex = viewPeersReportParam.getPageIndex();
        int pageSize = viewPeersReportParam.getPageSize();
        int student_id = id;*/

        Student student = studentMapper.selectByPrimaryKey(id);
        int teacher_id = student.getTeacherId();

        StudentExample studentExample = new StudentExample();
        studentExample.createCriteria().andTeacherIdEqualTo(teacher_id);

        ArrayList<Student> peers = (ArrayList<Student>) studentMapper.selectByExample(studentExample);
        ArrayList<Integer> peersids = new ArrayList<Integer>();
        for (int i = 0; i < peers.size(); i++) {
            peersids.add(peers.get(i).getId());
        }


        ReportExample reportExample = new ReportExample();
        reportExample.createCriteria().andStudentIdIn(peersids);
        reportExample.setOrderByClause("submit_time desc");
        List peersReport = reportMapper.selectByExampleWithBLOBs(reportExample);

        return peersReport;
    }

    //老师查看自己学生的周报
    public List viewStudentsReport(int id, ViewStudentsReportParam viewStudentsReportParam) {
        int teacher_id = id;

        StudentExample studentExample = new StudentExample();
        studentExample.createCriteria().andTeacherIdEqualTo(teacher_id);

        ArrayList<Student> students = (ArrayList<Student>) studentMapper.selectByExample(studentExample);

        if (students.size() == 0 || students == null)
            return new ArrayList();

        ArrayList<Integer> studentids = new ArrayList<Integer>();
        for (int i = 0; i < students.size(); i++) {
            studentids.add(students.get(i).getId());
        }
        // TODO 条件查询还没有起作用
        ReportExample reportExample = new ReportExample();
        ReportExample.Criteria criteria = reportExample.createCriteria().andStudentIdIn(studentids);
        if (viewStudentsReportParam != null) {
            criteria.andSubmitTimeStrGreaterThanOrEqualTo(viewStudentsReportParam.getStartDate())
                    .andSubmitTimeStrLessThanOrEqualTo(viewStudentsReportParam.getEndDate());
        }
        reportExample.setOrderByClause("submit_time desc");
        List peersReport = reportMapper.selectByExampleWithBLOBs(reportExample);

        if (peersReport.size() == 0 || peersReport == null)
            return new ArrayList();


        return peersReport;
    }

    /**
     * @return
     * @throws
     * @description TODO 将report下载为Word
     * @author Gcy
     * @date 2022/4/24 18:54
     **/
    @Override
    public boolean downDoc(List<ReportWithBLOBs> reports) {
        for (ReportWithBLOBs reportWithBLOBs : reports) {
            int studentId = reportWithBLOBs.getStudentId();
            String studentName = studentMapper.getStudentNameById(studentId);
            String userName = studentMapper.getStudentUserNameByid(studentId);
            String submitTime = new SimpleDateFormat("yyyy-MM-dd").format(reportWithBLOBs.getSubmitTime());
            String title = reportWithBLOBs.getTitle();
            String docName = userName + "-" + title;
            String thisWeek = reportWithBLOBs.getThisWeek();
            String bugMeet = reportWithBLOBs.getBugMeet();
            String nextWeek = reportWithBLOBs.getNextWeek();
            String reply = reportWithBLOBs.getReply();
            int pv = reportWithBLOBs.getPv();
            thisWeek = delHtmlTag(thisWeek);
            bugMeet = delHtmlTag(bugMeet);
            nextWeek = delHtmlTag(nextWeek);
            //reply = delHtmlTag(reply);

            Map<String, Object> map = new HashMap();

            map.put("studentName", studentName);
            map.put("submitTime", submitTime);
            map.put("docName", docName);
            map.put("thisWeek", thisWeek);
            map.put("bugMeet", bugMeet);
            map.put("nextWeek", nextWeek);
            if (reply == null) {
                reply = "未填写评语";
            }
            map.put("reply", reply);
            map.put("studentId", userName);
            map.put("pv", submitTime);
            export(map);
            System.out.println("test");
        }
        return true;
    }

    /**
     * @return String
     * @throws
     * @description TODO 去除HTML标签
     * @author Gcy
     * @date 2022/4/24 18:55
     **/
    public String delHtmlTag(String htmlStr) {
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        // 过滤空格等
        htmlStr = htmlStr.replaceAll(spaceRegex, "");
        // 过滤&nbsp;
        htmlStr = htmlStr.replace("&nbsp;", "");

        return htmlStr;
    }

    /**
     * @return
     * @throws
     * @description TODO 将周报内容导出为Word文档
     * @author Gcy
     * @date 2022/4/24 21:06
     **/
    public void export(Map<String, Object> map) {
        try {
            //String path = ReportServiceImpl.class.getResource("/").getPath();

            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
            //模板文件配置路径
            configuration.setDirectoryForTemplateLoading(new File("F:\\LabManage\\LabManage-main\\LabManage\\src\\main\\webapp\\WEB-INF\\word"));
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
            //文件输出路径,文件名
            File f2 = new File("E:\\组会周报\\" + map.get("studentName"));
            boolean flag2 = f2.mkdirs();         //使用mkdir()方法创建一个文件夹
            File outFile = new File("E:\\组会周报\\" + map.get("studentName") + "\\" + map.get("docName") + ".doc");
            //扫描模板路径下 模板文件
            Template template = configuration.getTemplate("groupMeetingTemplate.xml", "UTF-8");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"), 10240);
            template.process(map, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
