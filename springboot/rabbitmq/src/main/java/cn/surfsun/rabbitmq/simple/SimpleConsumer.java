package cn.surfsun.rabbitmq.simple;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.SimpleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 简单队列 - 消费者
 */
@Component
@RequiredArgsConstructor
public class SimpleConsumer {

    private final MessageLogService logService;

    @RabbitListener(queues = SimpleConfig.QUEUE)
    public void receive(String message) {
        logService.push(MessageLog.received("simple", "Consumer", "收到消息: " + message));
    }
}
