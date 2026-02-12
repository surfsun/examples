package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 插件延迟队列模式配置（基于 x-delayed-message 插件）
 * <p>
 * 【模式说明】使用 RabbitMQ 的 x-delayed-message 插件实现精准延迟消息。
 * 相比 TTL+DLX 方式，插件方式支持每条消息设置不同的延迟时间，且不存在队头阻塞问题。
 * <p>
 * 【架构图】
 *                   (消息暂存在Exchange)          延迟到期后投递
 * Producer ──▶ Delayed Exchange ─────────────────────────────▶ Queue ──▶ Consumer
 *               (x-delayed-message)
 * <p>
 * 【与 TTL+DLX 对比】
 * | 特性       | x-delayed-message 插件 | TTL + DLX 方式 |
 * |-----------|----------------------|---------------|
 * | 延迟精度   | ✅ 毫秒级精确           | ❌ 受FIFO限制   |
 * | 乱序延迟   | ✅ 支持               | ❌ 不支持       |
 * | 实现复杂度  | ✅ 简单               | ❌ 复杂         |
 * | 插件依赖   | ⚠️ 需要安装插件        | ✅ 原生支持      |
 */
@Configuration
public class PluginDelayConfig {

    public static final String EXCHANGE = "tutorial.plugin.delay.exchange";
    public static final String QUEUE = "tutorial.plugin.delay.queue";

    // 声明自定义类型的延迟交换机
    @Bean
    public CustomExchange pluginDelayExchange() {
        // 类型为 "x-delayed-message"，需要安装对应插件
        // x-delayed-type 指定底层使用的交换机类型（这里用 direct）
        return new CustomExchange(EXCHANGE, "x-delayed-message", true, false,
                Map.of("x-delayed-type", "direct"));
    }

    @Bean
    public Queue pluginDelayQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding pluginDelayBinding() {
        return BindingBuilder.bind(pluginDelayQueue()).to(pluginDelayExchange()).with(QUEUE).noargs();
    }
}
