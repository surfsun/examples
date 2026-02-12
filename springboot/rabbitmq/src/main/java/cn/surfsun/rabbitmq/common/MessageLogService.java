package cn.surfsun.rabbitmq.common;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 消息日志服务
 * <p>
 * 通过 SSE（Server-Sent Events）将消息日志实时推送到前端页面，
 * 让学习者无需查看后台控制台即可看到消费者处理结果。
 */
@Service
public class MessageLogService {

    /** 按模式名称管理 SSE 连接 */
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * 创建新的 SSE 连接
     */
    public SseEmitter subscribe(String pattern) {
        SseEmitter emitter = new SseEmitter(0L); // 不超时
        emitters.computeIfAbsent(pattern, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(pattern, emitter));
        emitter.onTimeout(() -> removeEmitter(pattern, emitter));
        emitter.onError(e -> removeEmitter(pattern, emitter));

        return emitter;
    }

    /**
     * 推送消息日志到前端
     */
    public void push(MessageLog log) {
        List<SseEmitter> list = emitters.get(log.getPattern());
        if (list == null) return;

        list.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("message-log")
                        .data(log));
            } catch (IOException e) {
                removeEmitter(log.getPattern(), emitter);
            }
        });
    }

    private void removeEmitter(String pattern, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(pattern);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
