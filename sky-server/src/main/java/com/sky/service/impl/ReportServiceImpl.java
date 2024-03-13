package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ReportMapper;
import com.sky.mapper.UserMapper;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements com.sky.service.ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额统计(指定时间区间内)
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = dateList(begin,end);
        String dateListS = StringUtils.join(dateList, ",");
        // 构建turnoverList
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList){
            // select sum(amount) from orders where order_time > dateMin and order_time < dateMax and status = 5
            LocalDateTime dateMin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateMax = LocalDateTime.of(date, LocalTime.MAX);
            Double turnover = Double.valueOf(orderMapper.getByOrderTimeAndStatus(dateMin,dateMax, Orders.COMPLETED));
            turnover = turnover == null ? 0.0:turnover;
            turnoverList.add(turnover);
        }
        String turnoverListS = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateListS)
                .turnoverList(turnoverListS)
                .build();
    }

    /**
     * 统计用户量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = dateList(begin,end);
        String dateListS = StringUtils.join(dateList, ",");
        // 存放每天新增用户量newUserList
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList){
            // select count(id) from user where create_time > dateMin and create_time < dateMax
            LocalDateTime dateMin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dateMax = LocalDateTime.of(date, LocalTime.MAX);
            Integer newUser = orderMapper.getTotalUserByCreateTime(dateMin,dateMax);
            newUser = newUser == null ? 0:newUser;
            newUserList.add(newUser);
        }
        String newUserListS = StringUtils.join(newUserList, ",");
        // 存放每天总用户量totalUserList
        List<Integer> totalUserList = new ArrayList<>();
        // LocalDateTime dateMin = LocalDateTime.of(begin, LocalTime.MIN);
        for (LocalDate date : dateList){
            // select count(id) from user where create_time > ? and create_time < dateMax
            LocalDateTime dateMax = LocalDateTime.of(date, LocalTime.MAX);
            Integer totalUser = orderMapper.getTotalUserByCreateTime(null,dateMax);
            totalUser = totalUser == null ? 0:totalUser;
            totalUserList.add(totalUser);
        }
        String totalUserListS = StringUtils.join(totalUserList, ",");
        return UserReportVO.builder()
                .dateList(dateListS)
                .newUserList(newUserListS)
                .totalUserList(totalUserListS)
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = dateList(begin,end);
        String dateListS = StringUtils.join(dateList, ",");
        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        //遍历dateList集合，查询每天的有效订单数和订单总数
        for (LocalDate date : dateList) {
            //查询每天的订单总数 select count(id) from orders where order_time > ? and order_time < ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);

            //查询每天的有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }

        //计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //计算时间区间内的有效订单数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            //计算订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        }

        return  OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);

        return orderMapper.getByOrderTimeAndStatus(begin,end,status);
    }

    /**
     * 构建dateList
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate>  dateList(LocalDate begin, LocalDate end){
        // 构建dateList
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        // 这里发生了内存溢出 进入死循环 begin.plusDays(1)会生成一个新对象
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }
}
