package cn.surfsun.rabbitmq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由 Controller - 所有页面导航集中管理
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping("/demo/simple")
    public String simple() { return "demo/simple"; }

    @GetMapping("/demo/work")
    public String work() { return "demo/work"; }

    @GetMapping("/demo/fanout")
    public String fanout() { return "demo/fanout"; }

    @GetMapping("/demo/direct")
    public String direct() { return "demo/direct"; }

    @GetMapping("/demo/topic")
    public String topic() { return "demo/topic"; }

    @GetMapping("/demo/dlx")
    public String dlx() { return "demo/dlx"; }

    @GetMapping("/demo/delay")
    public String delay() { return "demo/delay"; }

    @GetMapping("/demo/plugin-delay")
    public String pluginDelay() { return "demo/plugin-delay"; }

    @GetMapping("/quiz")
    public String quiz() { return "quiz"; }
}
