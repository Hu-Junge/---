package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据多个id查询多个套餐信息
     * @param ids
     * @return
     */
    List<Long> getByDishId(List<Long> ids);
}
