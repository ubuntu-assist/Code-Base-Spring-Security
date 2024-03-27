package com.itutorix.workshop.demo;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/management")
@Hidden
public class ManagementController {

    @GetMapping
    public String get() {
        return "GET:: Management Controller";
    }

    @PostMapping
    public String post() {
        return "POST:: Management Controller";
    }

    @PutMapping
    public String put() {
        return "PUT:: Management Controller";
    }

    @DeleteMapping
    public String delete() {
        return "DELETE:: Management Controller";
    }
}
