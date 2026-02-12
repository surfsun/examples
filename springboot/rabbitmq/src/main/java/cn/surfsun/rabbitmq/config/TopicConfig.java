package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 主题模式配置（Topic Exchange）
 * <p>
 * 【模式说明】Topic 模式使用 Topic Exchange（主题交换机）。
 * 消息根据通配符模式匹配 routing key，实现灵活的消息路由。
 * <p>
 * 【架构图】
 *                              routing key 模式匹配
 *                              ┌── "user.*"  ── Queue.user  ──▶ Consumer 1
 * Producer ──▶ Topic Exchange ─┤
 *                              └── "order.*" ── Queue.order ──▶ Consumer 2
 * <p>
 * 【通配符规则】
 * - * (星号)：精确匹配一个单词，例如 "user.*" 匹配 "user.create" 但不匹配 "user.info.detail"
 * - # (井号)：匹配零个或多个单词，例如 "log.#" 匹配 "log"、"log.info"、"log.info.detail"
 */
@Configuration
public class TopicConfig {

    public static final String EXCHANGE = "tutorial.topic.exchange";
    public static final String QUEUE_USER = "tutorial.topic.queue.user";
    public static final String QUEUE_ORDER = "tutorial.topic.queue.order";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue topicQueueUser() {
        return QueueBuilder.durable(QUEUE_USER).build();
    }

    @Bean
    public Queue topicQueueOrder() {
        return QueueBuilder.durable(QUEUE_ORDER).build();
    }

    // 使用通配符 * 匹配一个单词
    @Bean
    public Binding topicBindingUser() {
        return BindingBuilder.bind(topicQueueUser()).to(topicExchange()).with("user.*");
    }

    @Bean
    public Binding topicBindingOrder() {
        return BindingBuilder.bind(topicQueueOrder()).to(topicExchange()).with("order.*");
    }
}
