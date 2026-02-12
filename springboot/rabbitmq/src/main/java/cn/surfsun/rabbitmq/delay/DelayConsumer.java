package cn.surfsun.rabbitmq.delay;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DelayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 延迟队列 - 消费者（TTL + DLX 方式）
 */
@Component
@RequiredArgsConstructor
public class DelayConsumer {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final MessageLogService logService;

    @RabbitListener(queues = DelayConfig.PROCESS_QUEUE)
    public void receive(String message) {
        String time = LocalDateTime.now().format(FMT);
        logService.push(MessageLog.received("delay", "延迟消费者", "⏰ [" + time + "] 延迟消息到期: " + message));
    }
}
