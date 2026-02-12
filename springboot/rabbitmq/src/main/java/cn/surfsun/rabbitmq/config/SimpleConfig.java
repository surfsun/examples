package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 简单队列模式配置
 * <p>
 * 【模式说明】Simple Queue（简单队列）是 RabbitMQ 最基础的消息传递模式。
 * 一个生产者（Producer）发送消息到一个队列（Queue），
 * 一个消费者（Consumer）从队列中接收并处理消息。
 * <p>
 * 【架构图】
 * Producer  ──▶  Queue  ──▶  Consumer
 * <p>
 * 【核心概念】
 * - 这里没有 Exchange，消息直接发送到队列（实际上使用了默认的 Default Exchange）
 * - 默认交换机会自动将消息路由到与 routing key 同名的队列
 * - durable(true) 表示队列持久化，RabbitMQ 重启后队列不会丢失
 */
@Configuration
public class SimpleConfig {

    public static final String QUEUE = "tutorial.simple.queue";

    @Bean
    public Queue simpleQueue() {
        // QueueBuilder.durable() 创建持久化队列
        // 持久化队列在 RabbitMQ 重启后仍然存在
        return QueueBuilder.durable(QUEUE).build();
    }
}
