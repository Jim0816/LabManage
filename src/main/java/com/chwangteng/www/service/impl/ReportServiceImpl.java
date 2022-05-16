package com.chwangteng.www.service.impl;

import com.chwangteng.www.pojo.consts.ReportConfig;
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
import org.springframework.util.ResourceUtils;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Override
    public List viewMyReport(int id, ViewStudentsReportParam viewStudentsReportParam) {
        ReportExample reportExample = new ReportExample();
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        ReportExample.Criteria criteria = reportExample.createCriteria().andStudentIdIn(ids);
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
     * @description TODO 将report下载为Word.
     * @author Gcy
     * @date 2022/4/24 18:54
     **/
    @Override
    public void downDoc(List<ReportWithBLOBs> reports) {
        for (ReportWithBLOBs reportWithBLOBs : reports) {
            int studentId = reportWithBLOBs.getStudentId();
            String studentName = studentMapper.getStudentNameById(studentId);
            String userName = studentMapper.getStudentUserNameByid(studentId);
            String submitTime = new SimpleDateFormat("yyyy-MM-dd").format(reportWithBLOBs.getSubmitTime());
            String title = reportWithBLOBs.getTitle();
            String docName = submitTime + "-" + studentName + "-" + userName;
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
        }

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
            Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
            //模板文件配置路径
            File file = ResourceUtils.getFile("classpath:template/");
            configuration.setDirectoryForTemplateLoading(file);
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
            //文件输出路径,文件名
            File f2 = new File(ReportConfig.reportRootPath + map.get("studentName"));
            if(!f2.getParentFile().exists()) f2.getParentFile().mkdirs();         //使用mkdirs()方法创建一个文件夹
            File outFile = new File(ReportConfig.reportRootPath + map.get("studentName") + "/" + map.get("docName") + ".doc");
            if(!outFile.getParentFile().exists()) outFile.getParentFile().mkdirs();
            //扫描模板路径下 模板文件
            Template template = configuration.getTemplate("groupMeetingTemplate.xml", "UTF-8");
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"), 1024);
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
    /**
     * @description TODO 将文件夹压缩成zip 并且保留原目录结构
     * @return
     * @exception
     * @author Gcy
     * @date 2022/5/15 16:06
     **/
    @Override
    public void toZip(String srcDir, FileOutputStream out, boolean KeepDirStructure)
            throws RuntimeException{
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile,zos,sourceFile.getName(),KeepDirStructure);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

  /**
   * @description TODO 删除服务器上缓存文件
   * @return
   * @exception
   * @author Gcy
   * @date 2022/5/15 16:35
   **/
    public boolean del(String fileName) {
        File file = new File(fileName);  // fileName是路径或者file.getPath()获取的文件路径
        if (file.exists()) {
            if (file.isFile()) {
                return deleteFile(fileName);  // 是文件，调用删除文件的方法
            } else {
                return deleteDirectory(fileName);  // 是文件夹，调用删除文件夹的方法
            }
        } else {
            System.out.println("文件或文件夹删除失败：" + fileName);
            return false;
        }
    }


    /**
     * @description TODO 删除文件
     * @return 删除成功返回true,失败返回false
     * @exception
     * @param fileName 文件名
     * @author Gcy
     * @date 2022/5/15 16:35
     **/
    public boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            System.out.println("删除文件成功：" + fileName);
            return true;
        } else {
            System.out.println("删除文件失败：" + fileName);
            return false;
        }
    }

    /**
     * @description TODO 删除文件夹，删除文件夹需要把包含的文件及文件夹先删除，才能成功
     * @return 删除成功返回true,失败返回false
     * @exception
     * @param directory 文件名
     * @author Gcy
     * @date 2022/5/15 16:35
     **/
    public boolean deleteDirectory(String directory) {
        // directory不以文件分隔符（/或\）结尾时，自动添加文件分隔符，不同系统下File.separator方法会自动添加相应的分隔符
        if (!directory.endsWith(File.separator)) {
            directory = directory + File.separator;
        }
        File directoryFile = new File(directory);
        // 判断directory对应的文件是否存在，或者是否是一个文件夹
        if (!directoryFile.exists() || !directoryFile.isDirectory()) {
            System.out.println("文件夹删除失败，文件夹不存在" + directory);
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件和文件夹
        File[] files = directoryFile.listFiles();
        for (int i = 0; i < files.length; i++) {  // 循环删除所有的子文件及子文件夹
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {  // 删除子文件夹
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            System.out.println("删除失败");
            return false;
        }
        // 最后删除当前文件夹
        if (directoryFile.delete()) {
            System.out.println("删除成功：" + directory);
            return true;
        } else {
            System.out.println("删除失败：" + directory);
            return false;
        }
    }

    /**
    * @description TODO 递归压缩文件
    * @return
    * @exception
    * @author Gcy
    * @date 2022/5/15 16:06
    **/
    private static void compress(File sourceFile, ZipOutputStream zos, String name,
                                 boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[10240];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if(KeepDirStructure){
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }

                }
            }
        }
    }

}
