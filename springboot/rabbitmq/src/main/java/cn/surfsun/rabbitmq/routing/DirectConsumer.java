package cn.surfsun.rabbitmq.routing;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DirectConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 路由模式 - 消费者
 */
@Component
@RequiredArgsConstructor
public class DirectConsumer {

    private final MessageLogService logService;

    @RabbitListener(queues = DirectConfig.QUEUE_INFO)
    public void receiveInfo(String message) {
        logService.push(MessageLog.received("direct", "Info消费者", "处理INFO日志: " + message));
    }

    @RabbitListener(queues = DirectConfig.QUEUE_ERROR)
    public void receiveError(String message) {
        logService.push(MessageLog.received("direct", "Error消费者", "⚠️ 处理ERROR日志: " + message));
    }
}
