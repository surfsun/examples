package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 死信队列模式配置（Dead Letter Exchange）
 * <p>
 * 【模式说明】当消息无法被正常消费时（处理失败/TTL过期/队列已满），
 * 消息会自动被转发到死信交换机（DLX），进而路由到死信队列。
 * <p>
 * 【架构图】
 *                                        消息过期/拒绝/队列满
 * Producer ──▶ Business Queue ─────────────────────────────────▶ DLX Exchange ──▶ DLX Queue ──▶ Consumer
 *               (TTL=10s, 绑定DLX)                                                             (特殊处理)
 * <p>
 * 【触发死信的三种条件】
 * 1. 消息被拒绝（basic.reject / basic.nack）且 requeue=false
 * 2. 消息 TTL 过期
 * 3. 队列达到最大长度
 */
@Configuration
public class DlxConfig {

    public static final String DLX_EXCHANGE = "tutorial.dlx.exchange";
    public static final String DLX_QUEUE = "tutorial.dlx.queue";
    public static final String BUSINESS_QUEUE = "tutorial.business.queue";

    // 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // 死信队列（用于接收从业务队列转发过来的死信消息）
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    // 业务队列：配置死信交换机参数
    @Bean
    public Queue businessQueue() {
        return QueueBuilder.durable(BUSINESS_QUEUE)
                // 指定死信交换机
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                // 指定死信路由键
                .withArgument("x-dead-letter-routing-key", DLX_QUEUE)
                // 消息 TTL：10秒后过期自动进入死信队列
                .withArgument("x-message-ttl", 10000)
                .build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(DLX_QUEUE);
    }
}
