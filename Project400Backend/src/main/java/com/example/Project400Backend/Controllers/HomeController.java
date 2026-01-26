package com.example.Project400Backend.Controllers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello from Spring Boot";
    }
}
