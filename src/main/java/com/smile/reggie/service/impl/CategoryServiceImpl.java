package com.smile.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smile.reggie.common.CustomException;
import com.smile.reggie.entity.Category;
import com.smile.reggie.entity.Dish;
import com.smile.reggie.entity.Setmeal;
import com.smile.reggie.mapper.CategoryMapper;
import com.smile.reggie.service.CategoryService;
import com.smile.reggie.service.DishService;
import com.smile.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1=dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2=setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(id);

    }
}
