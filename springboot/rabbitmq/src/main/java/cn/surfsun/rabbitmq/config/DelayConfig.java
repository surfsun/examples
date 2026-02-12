package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延迟队列模式配置（基于 TTL + DLX 实现）
 * <p>
 * 【模式说明】利用消息 TTL（Time To Live）和死信队列实现延迟消息。
 * 消息发送到延迟队列后不会被消费，等 TTL 过期后自动转入处理队列。
 * <p>
 * 【架构图】
 *                                     TTL过期
 * Producer ──▶ Delay Queue ──────────────────▶ Delay Exchange ──▶ Process Queue ──▶ Consumer
 *               (设置TTL, 绑定DLX)                                                  (定时处理)
 * <p>
 * 【注意事项】
 * - TTL+DLX 方式存在队列头部阻塞问题：如果队列头部消息TTL较长，后面TTL较短的消息也无法先过期
 * - 适用于固定延迟时间的场景；如需不同延迟时间，推荐使用插件延迟队列
 */
@Configuration
public class DelayConfig {

    public static final String EXCHANGE = "tutorial.delay.exchange";
    public static final String DELAY_QUEUE = "tutorial.delay.queue";
    public static final String PROCESS_QUEUE = "tutorial.delay.process.queue";

    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(EXCHANGE);
    }

    // 延迟队列：消息在此等待TTL过期
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PROCESS_QUEUE)
                .build();
    }

    // 处理队列：接收过期后的消息
    @Bean
    public Queue delayProcessQueue() {
        return QueueBuilder.durable(PROCESS_QUEUE).build();
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayProcessQueue()).to(delayExchange()).with(PROCESS_QUEUE);
    }
}
