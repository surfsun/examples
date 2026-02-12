package cn.surfsun.rabbitmq.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitTemplate 全局配置
 * <p>
 * 【知识点】RabbitTemplate 是 Spring AMQP 提供的消息发送核心工具类，
 * 类似于 JdbcTemplate 的角色。它封装了与 RabbitMQ Broker 的连接管理、
 * 消息序列化、发送确认等底层细节。
 * <p>
 * 【知识点】MessageConverter 决定了消息在发送和接收时如何序列化/反序列化。
 * 默认使用 SimpleMessageConverter（仅支持 String/byte[]/Serializable），
 * 使用 Jackson2JsonMessageConverter 可以直接发送/接收 Java 对象。
 */
@Configuration
public class RabbitTemplateConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
