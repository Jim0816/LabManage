package com.chwangteng.www.service;

import com.chwangteng.www.param.ViewPeersReportParam;
import com.chwangteng.www.param.ViewStudentsReportParam;
import com.chwangteng.www.pojo.ReportWithBLOBs;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public interface ReportService {

    //学生查看同门的周报
    public List getPeersReport(int id, ViewPeersReportParam viewPeersReportParam);

    //老师查看自己学生的周报
    public List viewStudentsReport(int id, ViewStudentsReportParam viewStudentsReportParam);

    /**
     * @description TODO
     * @return 下载周报到服务器
     * @exception 
     * @author Gcy
     * @date 2022/5/15 16:07
     **/
    void downDoc(List<ReportWithBLOBs> reports);
    /**
     * @description TODO s
     * @return 压缩
     * @exception 
     * @author Gcy
     * @date 2022/5/15 16:07
     **/
    void toZip(String zipPath, FileOutputStream fos, boolean b);

    /**
     * @description TODO 删除服务器上的文件
     * @return
     * @exception
     * @author Gcy
     * @date 2022/5/15 16:20
     **/
    boolean del(String zipSourcePath);
}
