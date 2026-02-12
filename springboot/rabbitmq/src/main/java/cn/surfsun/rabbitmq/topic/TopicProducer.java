package cn.surfsun.rabbitmq.topic;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.TopicConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 主题模式 - 生产者
 */
@Service
@RequiredArgsConstructor
public class TopicProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String routingKey, String message) {
        rabbitTemplate.convertAndSend(TopicConfig.EXCHANGE, routingKey, message);
        logService.push(MessageLog.sent("topic", "[" + routingKey + "] " + message));
    }
}
