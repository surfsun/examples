package cn.surfsun.rabbitmq.pubsub;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.FanoutConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 发布/订阅 - 生产者
 */
@Service
@RequiredArgsConstructor
public class FanoutProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String news) {
        // Fanout Exchange 忽略 routing key，第二个参数传空字符串即可
        rabbitTemplate.convertAndSend(FanoutConfig.EXCHANGE, "", news);
        logService.push(MessageLog.sent("fanout", news));
    }
}
