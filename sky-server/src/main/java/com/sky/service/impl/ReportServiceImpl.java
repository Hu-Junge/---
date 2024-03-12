package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements com.sky.service.ReportService {

    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 营业额统计(指定时间区间内)
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 构建dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            dateList.add(begin.plusDays(1));
        }
        String dateListS = StringUtils.join(dateList, ",");
        // 构建turnoverList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            // select sum(amount) from orders where order_time > dateMin and order_time < dateMax and status = 5
            LocalDateTime dateMin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateMax = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = orderMapper.getByOrderTimeAndStatus(dateMin,dateMax, Orders.COMPLETED);
            turnover = turnover == null ? 0.0:turnover;
            turnoverList.add(turnover);
        }
        String turnoverListS = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateListS)
                .turnoverList(turnoverListS)
                .build();
    }
}
