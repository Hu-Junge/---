package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        // 属性拷贝
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        // 获取当前用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        // 判断购物车是否已经存在  根据SQL的写法这里有两种结果：null或只有1条数据
        List<ShoppingCart> select = shoppingCartMapper.select(shoppingCart);
        // 不存在--insert   存在--update
        if (select == null || select.size() < 1 ){
            // 判断菜品/套餐--添加菜品/套餐的名称、图片等信息
            Long dishId = shoppingCartDTO.getDishId();
            if (  dishId != null ){
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName()); // 名称
                shoppingCart.setImage(dish.getImage()); // 图片
                shoppingCart.setAmount(dish.getPrice()); // 价格
            } else {
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName()); // 名称
                shoppingCart.setImage(setmeal.getImage()); // 图片
                shoppingCart.setAmount(setmeal.getPrice()); // 价格
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }else{
            ShoppingCart shoppingCart1 = select.get(0);
            shoppingCart1.setNumber(shoppingCart1.getNumber()+1);
            shoppingCartMapper.updateNumberById(shoppingCart1);
        }

    }

    /**
     * 获取购物车信息
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartMapper.list(userId);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }
}
