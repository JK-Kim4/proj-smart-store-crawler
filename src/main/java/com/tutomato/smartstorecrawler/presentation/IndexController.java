package com.tutomato.smartstorecrawler.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @GetMapping(value = {"/", "/index"})
    public String indexPage(){
        return "index";
    }
}
