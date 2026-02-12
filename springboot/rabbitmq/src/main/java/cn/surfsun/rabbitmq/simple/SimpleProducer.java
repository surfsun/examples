package cn.surfsun.rabbitmq.simple;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.SimpleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 简单队列 - 生产者
 */
@Service
@RequiredArgsConstructor
public class SimpleProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String message) {
        // 直接发送到队列（使用默认交换机，routing key = 队列名）
        rabbitTemplate.convertAndSend(SimpleConfig.QUEUE, message);
        logService.push(MessageLog.sent("simple", message));
    }
}
