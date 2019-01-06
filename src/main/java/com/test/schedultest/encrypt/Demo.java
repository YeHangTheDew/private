package com.test.schedultest.encrypt;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.text.DecimalFormat;

@Controller
@RequestMapping("/test")
public class Demo {


    @Autowired
    StringEncryptor encryptor;

    @GetMapping("/")
    @ResponseBody
    public void test() {
        String username = encryptor.encrypt("root");
        System.out.println(username);
        String password = encryptor.encrypt("root");
        System.out.println(password);
    }

}
