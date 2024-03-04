package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.FlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private FlavorMapper flavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 菜品管理分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<Dish> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal(); // 获取记录总数
        List<Dish> records = page.getResult(); // 获取数据

        return new PageResult(total,records);
    }

    /**
     * 新增菜品和口味
     * @param dishDTO
     */
    @Override
    @Transactional // 注解方式的事务控制 保证原子性，对于两张表的操作要么全成功，要么全失败
    public void saveWithFlavor(DishDTO dishDTO) {
        // 拷贝(仅仅拷贝名字相同的属性)，以便进行插入/更新操作
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        // 向菜品表插入一条数据(批量插入)
        dishMapper.insert(dish);
        // 获取菜品id
        Long id = dish.getId();
        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors(); // 获取口味数据
        if(flavors != null && flavors.size() > 0){
            // 遍历flavors放入菜品id
            // flavor是为每一个遍历对象起的名字 {}花括号内是对每个对象的操作
            flavors.forEach(flavor -> {
                flavor.setDishId(id);
            });
            flavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品的状态 启售/停售
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 判断当前菜品是否存在于套餐中
        List<Long> setmealIds = setmealDishMapper.getByDishId(ids);
        if(setmealIds != null && setmealIds.size()>0 ){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 批量删除菜品表数据
        dishMapper.deleteByIds(ids);
        // 批量关联口味数据
        flavorMapper.deleteByDishIds(ids);


    }
}
