package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 超时查询
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from sky_take_out.orders where status=#{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime orderTime);

    /**
     * 更新订单
     * @param order
     */
    void update(Orders order);

    /**
     * 根据订单号和用户id查询订单
     * @param number
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.orders where user_id = #{userId} and number = #{number}")
    Orders getByNumberAndUserId(String number, Long userId);
    /**

     * 用于替换微信支付更新数据库状态的问题

     * @param orderStatus

     * @param orderPaidStatus

     */

    @Update("update sky_take_out.orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{check_out_time} where user_id = #{userId} and number = #{number}")

    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime check_out_time,Long userId ,String number);

    /**
     * 查询订单详细
     * @param id
     */
    @Select("select * from sky_take_out.orders where id = #{id}")
    Orders getById(Long id);

    Integer getByOrderTimeAndStatus(LocalDateTime dateMin, LocalDateTime dateMax, Integer status);

    /**
     * 统计新增用户量
     * @param dateMin
     * @param dateMax
     * @return
     */
    Integer getTotalUserByCreateTime(LocalDateTime dateMin, LocalDateTime dateMax);
}
