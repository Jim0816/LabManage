package com.chwangteng.www.controller;

import com.chwangteng.www.Utils.ConstVar;
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

import javax.servlet.http.HttpSession;
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
    public ModelAndView downDoc(@RequestBody DateParam dateParam, HttpSession session) {
        int currentteacher = Integer.parseInt(session.getAttribute(ConstVar._SESSION_USER_ID_).toString());
        ViewStudentsReportParam viewStudentsReportParam = null;
        if (dateParam != null && dateParam.getStartDate() != null && dateParam.getEndDate() != null) {
            viewStudentsReportParam = new ViewStudentsReportParam();
            viewStudentsReportParam.setStartDate(dateParam.getStartDate());
            viewStudentsReportParam.setEndDate(dateParam.getEndDate());
        }

        List<ReportWithBLOBs> reports = (List<ReportWithBLOBs>) reportService.viewStudentsReport(currentteacher, viewStudentsReportParam);
        if (reports.isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ConstVar._KEY_CODE_, ConstVar._ERROR_COMMON_);
            map.put(ConstVar._KEY_MESSAGE_, "此时间段无文档");
            return new ModelAndView(new MappingJackson2JsonView(), map);
        }
        boolean report = reportService.downDoc(reports);

        if (report) {
            // TODO 文件导出成功，准备返回前端用户（即前端用户下载）
            ModelAndView mv = new ModelAndView();
            mv.addObject(ConstVar._KEY_DATA_, reports);
            mv.setView(new MappingJackson2JsonView());
            return mv;
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ConstVar._KEY_CODE_, ConstVar._ERROR_COMMON_);
            map.put(ConstVar._KEY_MESSAGE_, "发生错误");
            return new ModelAndView(new MappingJackson2JsonView(), map);
        }
    }
}