package cn.surfsun.rabbitmq.work;

import cn.surfsun.rabbitmq.common.MessageLog;
import cn.surfsun.rabbitmq.common.MessageLogService;
import cn.surfsun.rabbitmq.config.WorkConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 工作队列 - 消费者（模拟 3 个工作者竞争消费）
 * <p>
 * 【知识点】多个 @RabbitListener 监听同一个队列时，
 * RabbitMQ 会以轮询（Round-Robin）方式分发消息。
 */
@Component
@RequiredArgsConstructor
public class WorkConsumer {

    private final MessageLogService logService;

    @RabbitListener(queues = WorkConfig.QUEUE)
    public void worker1(String task) throws InterruptedException {
        Thread.sleep(500); // 模拟处理耗时
        logService.push(MessageLog.received("work", "Worker-1", "处理完成: " + task));
    }

    @RabbitListener(queues = WorkConfig.QUEUE)
    public void worker2(String task) throws InterruptedException {
        Thread.sleep(800);
        logService.push(MessageLog.received("work", "Worker-2", "处理完成: " + task));
    }

    @RabbitListener(queues = WorkConfig.QUEUE)
    public void worker3(String task) throws InterruptedException {
        Thread.sleep(1200);
        logService.push(MessageLog.received("work", "Worker-3", "处理完成: " + task));
    }
}
