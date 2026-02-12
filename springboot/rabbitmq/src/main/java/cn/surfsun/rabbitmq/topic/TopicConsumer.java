package cn.surfsun.rabbitmq.topic;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.TopicConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 主题模式 - 消费者
 */
@Component
@RequiredArgsConstructor
public class TopicConsumer {

    private final MessageLogService logService;

    @RabbitListener(queues = TopicConfig.QUEUE_USER)
    public void receiveUser(String message) {
        logService.push(MessageLog.received("topic", "用户队列", "匹配 user.* → " + message));
    }

    @RabbitListener(queues = TopicConfig.QUEUE_ORDER)
    public void receiveOrder(String message) {
        logService.push(MessageLog.received("topic", "订单队列", "匹配 order.* → " + message));
    }
}
