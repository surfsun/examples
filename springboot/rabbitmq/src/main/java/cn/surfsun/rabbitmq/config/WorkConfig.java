package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工作队列模式配置
 * <p>
 * 【模式说明】Work Queue（工作队列）又称 Task Queue（任务队列）。
 * 多个消费者竞争性地从同一个队列中获取消息，每条消息只会被一个消费者处理。
 * <p>
 * 【架构图】
 *                    ┌──▶  Consumer 1
 * Producer  ──▶  Queue ──▶  Consumer 2
 *                    └──▶  Consumer 3
 * <p>
 * 【核心概念】
 * - 轮询分发：默认情况下 RabbitMQ 会平均分配消息给各消费者
 * - 公平分发：设置 prefetch=1 后，只有消费者处理完当前消息才会收到下一条
 * - 适用于 CPU 密集型任务的并行处理
 */
@Configuration
public class WorkConfig {

    public static final String QUEUE = "tutorial.work.queue";

    @Bean
    public Queue workQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }
}
