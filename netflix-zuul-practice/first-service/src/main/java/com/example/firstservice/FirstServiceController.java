package com.example.firstservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// gateway 적용 전 request mapping > http://localhost:8081/welcome
// gateway 적용 후 request mapping > http://localhost:8081/first-service/welcome

@RestController
@RequestMapping("/first-service")
public class FirstServiceController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the First Service.";
    }
}
