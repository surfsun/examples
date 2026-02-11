package cn.surfsun.springaialibaba.web;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * web搜索控制器
 */
@Slf4j
@Controller
public class WebSearchController {

    private final ChatClient chatClient;

    public WebSearchController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 联网搜索聊天页面
     */
    @GetMapping("/web/chat")
    public String webChatPage() {
        return "web-search";
    }

    /**
     * 联网搜索对话API（流式输出）
     */
    @GetMapping(value = "/api/web/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public Flux<String> chat(@RequestParam String message, @RequestParam String sessionId) {
        log.info("联网搜索对话 [Session: {}]: {}", sessionId, message);

        return chatClient.prompt()
                .options(DashScopeChatOptions.builder()
                        .withEnableSearch(true)
                        .build())
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, sessionId)
                        .param("chat_memory_retrieve_size", 10))
                .user(message)
                .stream()
                .content();
    }
}
