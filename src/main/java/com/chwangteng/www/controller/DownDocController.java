package com.chwangteng.www.controller;

import com.chwangteng.www.Utils.ConstVar;
import com.chwangteng.www.param.ViewStudentsReportParam;
import com.chwangteng.www.pojo.ReportWithBLOBs;
import com.chwangteng.www.pojo.consts.ReportConfig;
import com.chwangteng.www.pojo.dto.ReportParam;
import com.chwangteng.www.service.ReportService;
import com.chwangteng.www.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
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
    public ModelAndView downDoc(@RequestBody ReportParam reportParam, HttpServletRequest request, HttpSession session) throws IOException {
        List<ReportWithBLOBs> reports = null;
        int id = Integer.parseInt(session.getAttribute(ConstVar._SESSION_USER_ID_).toString());
        ViewStudentsReportParam viewStudentsReportParam = null;
        if (reportParam != null && reportParam.getStartDate() != null && reportParam.getEndDate() != null) {
            viewStudentsReportParam = new ViewStudentsReportParam();
            viewStudentsReportParam.setStartDate(reportParam.getStartDate());
            viewStudentsReportParam.setEndDate(reportParam.getEndDate());
        }

        if ("teacher".equals(reportParam.getIdentify())){
            // 教师下载周报
            reports = (List<ReportWithBLOBs>) reportService.viewStudentsReport(id, viewStudentsReportParam);
        }else{
            // 学生下载周报
            reports = (List<ReportWithBLOBs>) reportService.viewMyReport(id, viewStudentsReportParam);
        }


        String contextPath = request.getSession().getServletContext().getRealPath("/");
        ReportConfig.initReport(contextPath);
        File downloadPathFile = new File(contextPath + "/download");
        if (!downloadPathFile.exists()){
            downloadPathFile.mkdir();
        }else{
            reportService.del(downloadPathFile.getAbsolutePath());
        }

        // 先删除上一次的缓存记录zip文件
        //reportService.del(ReportConfig.zipPath);
        //reportService.del(ReportConfig.reportRootPath);

        // 导出周报到本地
        reportService.downDoc(reports);

        // 将周报压缩
        File file = new File(ReportConfig.zipPath);
        FileOutputStream fos = new FileOutputStream(file);
        reportService.toZip(ReportConfig.reportRootPath, fos, true);

        ModelAndView mv = new ModelAndView();
        Map<String, String> data = new HashMap<>();
        data.put("path", ReportConfig.zipPath);
        mv.addObject(ConstVar._KEY_DATA_, ReportConfig.path);
        mv.setView(new MappingJackson2JsonView());
        return mv;
    }
}