package com.smile.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smile.reggie.entity.SetmealDish;
import com.smile.reggie.mapper.SetmealDishMapper;
import com.smile.reggie.service.SetmealDishService;
import com.smile.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
