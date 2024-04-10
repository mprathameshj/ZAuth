package com.example.ZAuth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/test")
    public  String poke(){
        return "Lets begin the new journey";
    }

}
