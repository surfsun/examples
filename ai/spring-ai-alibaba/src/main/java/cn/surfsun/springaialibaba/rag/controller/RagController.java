package cn.surfsun.springaialibaba.rag.controller;

import cn.surfsun.springaialibaba.rag.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * RAG控制器
 */
@Slf4j
@Controller
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 聊天页面
     */
    @GetMapping("/rag/chat")
    public String chatPage() {
        return "chat";
    }

    /**
     * RAG对话API
     */
    @PostMapping("/rag/chat")
    @ResponseBody
    public String chat(@RequestParam String message, jakarta.servlet.http.HttpSession session) {
        return ragService.chatWithRag(message, session.getId());
    }
}
