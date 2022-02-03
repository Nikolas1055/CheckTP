package ru.esk.checktp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//TODO тест отправки письма, переделать
@Controller
public class SimpleEmailExampleController {

    private final JavaMailSender mailSender;

    @Autowired
    public SimpleEmailExampleController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @ResponseBody
    @RequestMapping("/sendSimpleEmail")
    public String sendSimpleEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("kny@esk-nnov.ru");
        message.setSubject("Test Simple Email");
        message.setText("Hello, Im testing Simple Email");

        this.mailSender.send(message);

        return "Email Sent!";
    }
}
