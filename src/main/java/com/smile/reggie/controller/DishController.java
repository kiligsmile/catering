package com.smile.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smile.reggie.common.R;
import com.smile.reggie.dto.DishDto;
import com.smile.reggie.entity.Category;
import com.smile.reggie.entity.Dish;
import com.smile.reggie.entity.DishFlavor;
import com.smile.reggie.entity.Employee;
import com.smile.reggie.service.CategoryService;
import com.smile.reggie.service.DishFlavorService;
import com.smile.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        //        清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

//        清理某个分类下的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records=pageInfo.getRecords();
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId= item.getCategoryId();;
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName= category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto=dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

//        清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

//        清理某个分类下的菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("修改菜品成功");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list=dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        dishDtoList =(List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list=dishService.list(queryWrapper);
        dishDtoList=list.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId= item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if(category!=null){
                String categoryName= category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId=item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
        return R.success(dishDtoList);
    }
}
