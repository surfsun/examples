package cn.surfsun.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RabbitMQ 教学项目启动类
 * <p>
 * 本项目演示 RabbitMQ 的 8 种经典消息模式，
 * 并提供知识问答功能用于考核学习效果。
 */
@SpringBootApplication
public class RabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqApplication.class, args);
    }
}
