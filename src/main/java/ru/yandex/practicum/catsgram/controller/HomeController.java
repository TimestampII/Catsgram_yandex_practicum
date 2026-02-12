package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/home")
    public String homePage() {
        return "<h1>Приветствуем вас в приложение Котограм<h1>";
    }
    @RequestMapping("/companis")
    public String companisPage(){
        return "<h3>  Это страница с выводом списка компаний <h3>";
    }
}
