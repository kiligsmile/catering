package com.smile.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smile.reggie.common.R;
import com.smile.reggie.entity.User;
import com.smile.reggie.service.UserService;
import com.smile.reggie.utils.SMSUtils;
import com.smile.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        String phone= user.getPhone();
        String[] letters = new String[]{
                "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m",
                "A", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z", "X", "C", "V", "B", "N", "M",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            stringBuilder.append(letters[(int) Math.floor(Math.random() * letters.length)]);
        }
        //获取6位随机验证码（中文），根据项目需要选择中英文
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        System.out.println("验证码为："+code);
        if(StringUtils.isNotEmpty(phone)){
            SMSUtils.sendMessage(phone,code);
//            将生成的验证码保存在session中
//            session.setAttribute(phone,code);

//            将生成的验证码保存在Redis中，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code, 5,TimeUnit.MINUTES);

            return  R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        String phone= map.get("phone").toString();
        String code = map.get("code").toString();
//        从session中取验证码
//        Object codeInSession =session.getAttribute(phone);

//        从Redis中取验证码
        Object codeInSession=  redisTemplate.opsForValue().get(phone);

        if(codeInSession!=null&&codeInSession.equals(code)){
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user=userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

//            如果用户登录成功，删除Redis中缓存的验证码
             redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
