package cn.surfsun.rabbitmq.dlx;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.DlxConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 死信队列 - 生产者
 */
@Service
@RequiredArgsConstructor
public class DlxProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    /** 发送消息到业务队列（消息会因处理失败或TTL过期进入死信队列） */
    public void send(String message) {
        rabbitTemplate.convertAndSend(DlxConfig.BUSINESS_QUEUE, message);
        logService.push(MessageLog.sent("dlx", message));
    }

    /** 发送带自定义 TTL 的消息 */
    public void sendWithTtl(String message, int ttl) {
        rabbitTemplate.convertAndSend(DlxConfig.BUSINESS_QUEUE, message, msg -> {
            msg.getMessageProperties().setExpiration(String.valueOf(ttl));
            return msg;
        });
        logService.push(MessageLog.sent("dlx", "[TTL=" + ttl + "ms] " + message));
    }
}
