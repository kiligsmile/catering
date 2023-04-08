package com.smile.reggie.utils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

public class ValidateCodeUtils {
    public static MimeMessage createSimpleMail(Session session, String receiveMail,String code) throws Exception {
        //  获取6位随机验证码（英文）
        String verifyCode=code;
        // 创建邮件对象
        MimeMessage message = new MimeMessage(session);
        // 指明邮件的发件人
        message.setFrom(new InternetAddress("1636625079@qq.com"));
        // 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMail));
        // 邮件的标题
        message.setSubject("验证码");
        // 邮件的文本内容
        message.setContent("您的验证码：" + verifyCode + "，如非本人操作，请忽略！请勿回复此邮箱", "text/html;charset=UTF-8");

        // 返回创建好的邮件对象
        return message;
    }
}
