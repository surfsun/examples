package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布/订阅模式配置（Fanout Exchange）
 * <p>
 * 【模式说明】Publish/Subscribe 模式使用 Fanout Exchange（扇出交换机）。
 * 生产者将消息发送到 Exchange，Exchange 会将消息复制并广播到所有绑定的队列。
 * <p>
 * 【架构图】
 *                              ┌── Queue1 ──▶ Consumer 1
 * Producer ──▶ Fanout Exchange ┤
 *                              └── Queue2 ──▶ Consumer 2
 * <p>
 * 【核心概念】
 * - Fanout Exchange 会忽略 routing key，将消息发送到所有绑定的队列
 * - 每个消费者都会收到相同的消息副本
 * - 典型场景：系统广播通知、新闻推送
 */
@Configuration
public class FanoutConfig {

    public static final String EXCHANGE = "tutorial.fanout.exchange";
    public static final String QUEUE_1 = "tutorial.fanout.queue.1";
    public static final String QUEUE_2 = "tutorial.fanout.queue.2";

    // 声明 Fanout Exchange
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(EXCHANGE);
    }

    // 声明两个队列，模拟两个订阅者
    @Bean
    public Queue fanoutQueue1() {
        return QueueBuilder.durable(QUEUE_1).build();
    }

    @Bean
    public Queue fanoutQueue2() {
        return QueueBuilder.durable(QUEUE_2).build();
    }

    // 将队列绑定到 Fanout Exchange（无需 routing key）
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }
}
