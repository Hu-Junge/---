package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 动态条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> select(ShoppingCart shoppingCart);

    /**
     * 修改购物车菜品数量
     * @param shoppingCart1
     */
    @Update("update sky_take_out.shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart1);

    /**
     * 插入购物车数据
     * @param shoppingCart
     */
    @Insert("insert into sky_take_out.shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time)" +
            " VALUES (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查询购物车数据
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.shopping_cart where user_id = #{userId}")
    List<ShoppingCart> list(Long userId);
}
