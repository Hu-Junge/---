package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper {
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into sky_take_out.dish(name, category_id, price, image, description, status," +
            " create_time, update_time, create_user, update_user)" +
            "values (#{name},#{categroryId},#{price},#{image},#{descriprion},#{status},#{createTime},#{updateTime},#{createUser}" +
            ",#{updateUser})")
    void insert(Dish dish);
}
