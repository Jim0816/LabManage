package com.chwangteng.www.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DateParam
 * @Description
 * @Author Jim
 * @Date 2022/4/20 17:25
 **/
@Data
@NoArgsConstructor
public class ReportParam {
    private String startDate;
    private String endDate;
    private String identify;
}
