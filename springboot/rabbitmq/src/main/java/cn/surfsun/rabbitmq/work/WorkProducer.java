package cn.surfsun.rabbitmq.work;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.WorkConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 工作队列 - 生产者
 */
@Service
@RequiredArgsConstructor
public class WorkProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MessageLogService logService;

    public void send(String task) {
        rabbitTemplate.convertAndSend(WorkConfig.QUEUE, task);
        logService.push(MessageLog.sent("work", "任务: " + task));
    }
}
