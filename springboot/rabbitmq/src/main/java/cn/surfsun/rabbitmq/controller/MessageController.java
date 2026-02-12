package cn.surfsun.rabbitmq.controller;

import cn.surfsun.rabbitmq.simple.SimpleProducer;
import cn.surfsun.rabbitmq.work.WorkProducer;
import cn.surfsun.rabbitmq.pubsub.FanoutProducer;
import cn.surfsun.rabbitmq.routing.DirectProducer;
import cn.surfsun.rabbitmq.topic.TopicProducer;
import cn.surfsun.rabbitmq.dlx.DlxProducer;
import cn.surfsun.rabbitmq.delay.DelayProducer;
import cn.surfsun.rabbitmq.plugindelay.PluginDelayProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 消息发送 API Controller
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final SimpleProducer simpleProducer;
    private final WorkProducer workProducer;
    private final FanoutProducer fanoutProducer;
    private final DirectProducer directProducer;
    private final TopicProducer topicProducer;
    private final DlxProducer dlxProducer;
    private final DelayProducer delayProducer;
    private final PluginDelayProducer pluginDelayProducer;

    @PostMapping("/simple")
    public ResponseEntity<Map<String, String>> sendSimple(@RequestParam String message) {
        simpleProducer.send(message);
        return ResponseEntity.ok(Map.of("status", "ok", "message", message));
    }

    @PostMapping("/work")
    public ResponseEntity<Map<String, String>> sendWork(@RequestParam String task) {
        workProducer.send(task);
        return ResponseEntity.ok(Map.of("status", "ok", "task", task));
    }

    @PostMapping("/fanout")
    public ResponseEntity<Map<String, String>> sendFanout(@RequestParam String news) {
        fanoutProducer.send(news);
        return ResponseEntity.ok(Map.of("status", "ok", "news", news));
    }

    @PostMapping("/direct/{level}")
    public ResponseEntity<Map<String, String>> sendDirect(@PathVariable String level, @RequestParam String message) {
        if ("info".equals(level)) {
            directProducer.sendInfo(message);
        } else {
            directProducer.sendError(message);
        }
        return ResponseEntity.ok(Map.of("status", "ok", "level", level, "message", message));
    }

    @PostMapping("/topic")
    public ResponseEntity<Map<String, String>> sendTopic(@RequestParam String routingKey, @RequestParam String message) {
        topicProducer.send(routingKey, message);
        return ResponseEntity.ok(Map.of("status", "ok", "routingKey", routingKey, "message", message));
    }

    @PostMapping("/dlx")
    public ResponseEntity<Map<String, String>> sendDlx(@RequestParam String message) {
        dlxProducer.send(message);
        return ResponseEntity.ok(Map.of("status", "ok", "message", message));
    }

    @PostMapping("/dlx/ttl")
    public ResponseEntity<Map<String, String>> sendDlxTtl(@RequestParam String message, @RequestParam int ttl) {
        dlxProducer.sendWithTtl(message, ttl);
        return ResponseEntity.ok(Map.of("status", "ok", "message", message, "ttl", String.valueOf(ttl)));
    }

    @PostMapping("/delay")
    public ResponseEntity<Map<String, String>> sendDelay(@RequestParam String message, @RequestParam int delayMs) {
        delayProducer.send(message, delayMs);
        return ResponseEntity.ok(Map.of("status", "ok", "message", message, "delayMs", String.valueOf(delayMs)));
    }

    @PostMapping("/plugin-delay")
    public ResponseEntity<Map<String, String>> sendPluginDelay(@RequestParam String message, @RequestParam int delayMs) {
        pluginDelayProducer.send(message, delayMs);
        return ResponseEntity.ok(Map.of("status", "ok", "message", message, "delayMs", String.valueOf(delayMs)));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "running", "service", "RabbitMQ Tutorial"));
    }
}
