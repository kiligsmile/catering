package com.smile.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smile.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
