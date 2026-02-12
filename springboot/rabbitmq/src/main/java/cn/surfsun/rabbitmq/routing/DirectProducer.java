package cn.surfsun.rabbitmq.routing;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DirectConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 路由模式 - 生产者
 */
@Service
@RequiredArgsConstructor
public class DirectProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void sendInfo(String message) {
        rabbitTemplate.convertAndSend(DirectConfig.EXCHANGE, "info", message);
        logService.push(MessageLog.sent("direct", "[INFO] " + message));
    }

    public void sendError(String message) {
        rabbitTemplate.convertAndSend(DirectConfig.EXCHANGE, "error", message);
        logService.push(MessageLog.sent("direct", "[ERROR] " + message));
    }
}
