package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品管理分类查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品管理页分类查询参数：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增菜品和口味
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增菜品和口味")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品和口味信息：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        // 清理对应的缓存数据
        String key = "dish_"+dishDTO.getCategoryId();
        this.cleanCache(key);

        return Result.success();
    }

    /**
     * 批量删除菜品
     * @param ids
     * @return
     */
    // TODO @RequestParam这个注解不理解
    // https://blog.csdn.net/zhangqunshuai/article/details/80660974 set/list/map的区别
    @DeleteMapping()
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品...");
        dishService.deleteBatch(ids);
        this.cleanCache("dish_*");

        return Result.success();
    }

    /**
     * 根据id查询菜品
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("查询菜品id:{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
     *修改菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改菜品信息")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息为：{}",dishDTO);
        dishService.update(dishDTO);

        this.cleanCache("dish_*");

        return Result.success();
    }

    // TODO 起售/停售 待开发
    public Result startAndStop(Long id){

        this.cleanCache("dish_*");
        return null;
    }

    /**
     * (私有方法)清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        // 获取所有以pattern开头的key
        Set keys = redisTemplate.keys(pattern);
        // 删除所有
        redisTemplate.delete(keys);
    }

}
