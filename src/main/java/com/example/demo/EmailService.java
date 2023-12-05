package com.example.demo;

import com.example.demo.User.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public boolean sendChangePasswordMail(SiteUser user, String tempPassword){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject("[데모] 임시 비밀번호 발급");
        simpleMailMessage.setFrom("gmlehd0201@gmail.com");
        simpleMailMessage.setText("임시비밀번호 발급 : " + tempPassword);

        try{
            javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
