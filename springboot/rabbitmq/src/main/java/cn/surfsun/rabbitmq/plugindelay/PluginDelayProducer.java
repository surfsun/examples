package cn.surfsun.rabbitmq.plugindelay;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.PluginDelayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 插件延迟队列 - 生产者（x-delayed-message 插件方式）
 */
@Service
@RequiredArgsConstructor
public class PluginDelayProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String message, int delayMs) {
        rabbitTemplate.convertAndSend(PluginDelayConfig.EXCHANGE, PluginDelayConfig.QUEUE, message, msg -> {
            // 通过 header 设置延迟时间（毫秒）
            msg.getMessageProperties().setHeader("x-delay", delayMs);
            return msg;
        });
        logService.push(MessageLog.sent("plugindelay", "[插件延迟" + delayMs + "ms] " + message));
    }
}
