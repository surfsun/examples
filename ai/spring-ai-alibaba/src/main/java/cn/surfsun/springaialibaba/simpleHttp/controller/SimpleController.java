package cn.surfsun.springaialibaba.simpleHttp.controller;

import cn.surfsun.springaialibaba.simpleHttp.service.SimpleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

    private final SimpleService simpleService;

    public SimpleController(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @GetMapping("/simple/chat")
    public String chat(@RequestParam String message) {
        return simpleService.chat(message);
    }
}
