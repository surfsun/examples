package cn.surfsun.rabbitmq.dlx;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DlxConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 死信队列 - 消费者
 */
@Component
@RequiredArgsConstructor
public class DlxConsumer {

    private final MessageLogService logService;

    /** 业务队列消费者：模拟处理失败，消息将进入死信队列 */
    @RabbitListener(queues = DlxConfig.BUSINESS_QUEUE)
    public void receiveBusiness(String message) {
        logService.push(MessageLog.received("dlx", "业务消费者", "❌ 处理失败: " + message));
        // 抛出异常模拟业务处理失败 → 消息进入死信队列
        throw new RuntimeException("模拟业务处理失败");
    }

    /** 死信队列消费者：处理无法正常消费的消息 */
    @RabbitListener(queues = DlxConfig.DLX_QUEUE)
    public void receiveDlx(String message) {
        logService.push(MessageLog.received("dlx", "死信消费者", "♻️ 死信处理: " + message));
    }
}
