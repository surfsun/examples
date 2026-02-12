package cn.surfsun.rabbitmq.pubsub;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.FanoutConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 发布/订阅 - 消费者（两个订阅者同时收到消息）
 */
@Component
@RequiredArgsConstructor
public class FanoutConsumer {

    private final MessageLogService logService;

    @RabbitListener(queues = FanoutConfig.QUEUE_1)
    public void subscriber1(String news) {
        logService.push(MessageLog.received("fanout", "订阅者-1", "收到广播: " + news));
    }

    @RabbitListener(queues = FanoutConfig.QUEUE_2)
    public void subscriber2(String news) {
        logService.push(MessageLog.received("fanout", "订阅者-2", "收到广播: " + news));
    }
}
