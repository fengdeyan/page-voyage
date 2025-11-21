package me.yan.controller;

import me.yan.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {
    @Autowired
    private Commons commons;
    @RequestMapping("/test")
    public String test(Model model) {
        System.out.println("enter test");
        System.out.printf("png===="+ commons.random(5, ".png"));
        model.addAttribute("commons", commons);
        return "admin/comment_list";
    }
}
