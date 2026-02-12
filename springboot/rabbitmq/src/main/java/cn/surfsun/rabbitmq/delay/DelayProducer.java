package cn.surfsun.rabbitmq.delay;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DelayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 延迟队列 - 生产者（TTL + DLX 方式）
 */
@Service
@RequiredArgsConstructor
public class DelayProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String message, int delayMs) {
        rabbitTemplate.convertAndSend(DelayConfig.DELAY_QUEUE, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(delayMs));
            return msg;
        });
        logService.push(MessageLog.sent("delay", "[延迟" + delayMs + "ms] " + message));
    }
}
