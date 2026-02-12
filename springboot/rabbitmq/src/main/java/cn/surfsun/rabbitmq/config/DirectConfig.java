package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 路由模式配置（Direct Exchange）
 * <p>
 * 【模式说明】Routing 模式使用 Direct Exchange（直连交换机）。
 * 消息根据精确匹配的 routing key 路由到对应的队列。
 * <p>
 * 【架构图】
 *                               routing key="info"
 *                              ┌─────────────────── Queue.info ──▶ Consumer 1
 * Producer ──▶ Direct Exchange ┤
 *                              └─────────────────── Queue.error ──▶ Consumer 2
 *                               routing key="error"
 * <p>
 * 【核心概念】
 * - Direct Exchange 根据 routing key 精确匹配，将消息路由到对应队列
 * - 一个队列可以绑定多个 routing key
 * - 多个队列可以绑定相同的 routing key（消息会被复制到每个匹配的队列）
 */
@Configuration
public class DirectConfig {

    public static final String EXCHANGE = "tutorial.direct.exchange";
    public static final String QUEUE_INFO = "tutorial.direct.queue.info";
    public static final String QUEUE_ERROR = "tutorial.direct.queue.error";

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue directQueueInfo() {
        return QueueBuilder.durable(QUEUE_INFO).build();
    }

    @Bean
    public Queue directQueueError() {
        return QueueBuilder.durable(QUEUE_ERROR).build();
    }

    // 绑定时指定 routing key
    @Bean
    public Binding directBindingInfo() {
        return BindingBuilder.bind(directQueueInfo()).to(directExchange()).with("info");
    }

    @Bean
    public Binding directBindingError() {
        return BindingBuilder.bind(directQueueError()).to(directExchange()).with("error");
    }
}
