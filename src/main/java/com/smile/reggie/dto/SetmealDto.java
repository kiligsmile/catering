package com.smile.reggie.dto;

import com.smile.reggie.entity.Setmeal;
import com.smile.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
