package com.smile.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smile.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
