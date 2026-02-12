package cn.surfsun.rabbitmq.plugindelay;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.PluginDelayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 插件延迟队列 - 消费者
 */
@Component
@RequiredArgsConstructor
public class PluginDelayConsumer {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final MessageLogService logService;

    @RabbitListener(queues = PluginDelayConfig.QUEUE)
    public void receive(String message) {
        String time = LocalDateTime.now().format(FMT);
        logService.push(MessageLog.received("plugindelay", "插件延迟消费者",
                "⏰ [" + time + "] 插件延迟到期: " + message));
    }
}
