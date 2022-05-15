package com.chwangteng.www.controller;

import com.chwangteng.www.Utils.ConstVar;
import com.chwangteng.www.Utils.DocUtil;
import com.chwangteng.www.param.ViewStudentsReportParam;
import com.chwangteng.www.pojo.ReportWithBLOBs;
import com.chwangteng.www.pojo.dto.DateParam;
import com.chwangteng.www.service.ReportService;
import com.chwangteng.www.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DownDocController
 * @Description
 * @Author Gcy
 * @Date 2022/4/23 15:12
 **/
@Controller
@RequestMapping("/down")
public class DownDocController {

    @Autowired
    ReportService reportService;

    @Autowired
    StudentService studentService;

    @RequestMapping("/downDoc.action")
    public void downDoc(@RequestBody DateParam dateParam, HttpSession session , HttpServletResponse response) throws IOException {
        int currentteacher = Integer.parseInt(session.getAttribute(ConstVar._SESSION_USER_ID_).toString());
        ViewStudentsReportParam viewStudentsReportParam = null;
        if (dateParam != null && dateParam.getStartDate() != null && dateParam.getEndDate() != null) {
            viewStudentsReportParam = new ViewStudentsReportParam();
            viewStudentsReportParam.setStartDate(dateParam.getStartDate());
            viewStudentsReportParam.setEndDate(dateParam.getEndDate());
        }

        List<ReportWithBLOBs> reports = (List<ReportWithBLOBs>) reportService.viewStudentsReport(currentteacher, viewStudentsReportParam);

        reportService.downDoc(reports);


        //提供下载文件前进行压缩，即服务端生成压缩文件
        File file = new File(DocUtil.zipPath);
        FileOutputStream fos = new FileOutputStream(file);
        reportService.toZip(DocUtil.zipSourcePath, fos, true);
        //1.获取要下载的文件的绝对路径
        String realPath = DocUtil.zipPath;
        //2.获取要下载的文件名
        String fileName = realPath.substring(realPath.lastIndexOf(File.separator)+1);
        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        //3.设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.addHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(),"utf-8"));
        //获取文件输入流
        InputStream in = new FileInputStream(realPath);
        int len = 0;
        byte[] buffer = new byte[DocUtil.BUFFERSIZE];
        OutputStream out = response.getOutputStream();
        while ((len = in.read(buffer)) > 0) {
            //将缓冲区的数据输出到客户端浏览器
            out.write(buffer,0,len);
        }
        in.close();
        out.close();
        //删除服务器缓存文件
        System.out.println(reportService.del(DocUtil.zipSourcePath));
        System.out.println(reportService.del(DocUtil.zipPath));
    }
}