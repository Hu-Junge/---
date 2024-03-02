package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlavorMapper {
    /**
     * 口味数据批量插入
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);
}
