package com.smile.reggie.utils;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.smile.reggie.utils.ValidateCodeUtils.createSimpleMail;

public class SMSUtils {
    public static void sendMessage(String receiveMail,String code){
        Properties prop=new Properties();
        prop.setProperty("mail.debug","true");
        prop.setProperty("mail.host","smtp.qq.com");
        prop.setProperty("mail.smtp.auth","true");
        prop.setProperty("mail.transport.protocol","smtp");
        try{
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put("mail.smtp.ssl.enable", "true");
            prop.put("mail.smtp.ssl.socketFactory", sf);
            Session session = Session.getInstance(prop);
            Transport ts = session.getTransport();
            ts.connect("smtp.qq.com", "1636625079@qq.com", "hqmvzsubymdvggaj");
//            邮件接收者
            Message message = createSimpleMail(session, receiveMail,code);
            ts.sendMessage(message, message.getAllRecipients());
            ts.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
