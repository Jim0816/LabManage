package com.chwangteng.www.param;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewStudentsReportParam {

    //用于分页
    private Integer pageIndex;
    private Integer pageSize;

    private String startDate;

    private String endDate;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getStartDate() {
        return startDate;
    }
    public Date getStartDateForDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTime = null;
        try {
            dateTime =  simpleDateFormat.parse(startDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dateTime;
    }

    public Date getEndDateForDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTime = null;
        try {
            dateTime =  simpleDateFormat.parse(endDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dateTime;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
