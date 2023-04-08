package com.smile.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smile.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
