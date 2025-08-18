package com.travel.wanderlog.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // controller REST (base)
public class PingController {

    @GetMapping("/api/ping") // mappa HTTP GET
    public String ping() {
        return "pong";
    }
}
