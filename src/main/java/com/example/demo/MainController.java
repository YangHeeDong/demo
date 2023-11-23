package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/index")
    @ResponseBody
    public String index(){
        return "인덱스";
    }

    @GetMapping("/")
    public String root(){
        return "redirect:/question/list";
    }

}
