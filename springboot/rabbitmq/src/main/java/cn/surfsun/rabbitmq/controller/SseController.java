package cn.surfsun.rabbitmq.controller;

import cn.surfsun.rabbitmq.common.MessageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 消息日志推送接口
 */
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final MessageLogService logService;

    /**
     * 订阅指定模式的消息日志流
     * 前端通过 EventSource 连接此接口，实时接收消费者处理结果
     */
    @GetMapping(value = "/{pattern}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String pattern) {
        return logService.subscribe(pattern);
    }
}
