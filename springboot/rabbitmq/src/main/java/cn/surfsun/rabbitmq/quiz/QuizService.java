package cn.surfsun.rabbitmq.quiz;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识问答服务 - 内置题库
 */
@Service
public class QuizService {

    private final List<QuizQuestion> questions = new ArrayList<>();

    public QuizService() {
        initQuestions();
    }

    /** 按分类获取题目 */
    public List<QuizQuestion> getByCategory(String category) {
        if ("all".equals(category)) return questions;
        return questions.stream()
                .filter(q -> q.getCategory().equals(category))
                .collect(Collectors.toList());
    }

    /** 获取随机题目 */
    public List<QuizQuestion> getRandom(int count) {
        List<QuizQuestion> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    /** 校验答案 */
    public boolean check(int questionId, int selectedAnswer) {
        return questions.stream()
                .filter(q -> q.getId() == questionId)
                .findFirst()
                .map(q -> q.getAnswer() == selectedAnswer)
                .orElse(false);
    }

    private void initQuestions() {
        // ===== 基础题 =====
        questions.add(new QuizQuestion(1, "basic",
                "RabbitMQ 使用的消息协议是什么？",
                List.of("HTTP", "AMQP", "MQTT", "STOMP"), 1,
                "RabbitMQ 基于 AMQP（Advanced Message Queuing Protocol，高级消息队列协议）实现。AMQP 是一个应用层协议，定义了消息中间件的通信标准。"));

        questions.add(new QuizQuestion(2, "basic",
                "在简单队列模式中，消息实际上是通过哪个 Exchange 投递的？",
                List.of("Fanout Exchange", "Direct Exchange", "Default Exchange（默认交换机）", "Topic Exchange"), 2,
                "当生产者直接发送消息到队列时，实际上使用了默认交换机（空字符串交换机）。默认交换机是一个 Direct Exchange，会自动将消息路由到与 routing key 同名的队列。"));

        questions.add(new QuizQuestion(3, "basic",
                "Work Queue 模式中，多个消费者的默认分发策略是什么？",
                List.of("随机分发", "轮询分发（Round-Robin）", "优先级分发", "广播分发"), 1,
                "默认情况下 RabbitMQ 使用轮询（Round-Robin）方式将消息平均分发给各个消费者。可以通过设置 prefetch 实现公平分发。"));

        questions.add(new QuizQuestion(4, "basic",
                "Fanout Exchange 会如何处理 routing key？",
                List.of("根据 routing key 精确匹配", "根据 routing key 模式匹配", "完全忽略 routing key", "将 routing key 作为队列名"), 2,
                "Fanout Exchange（扇出交换机）会忽略消息的 routing key，将消息广播到所有绑定的队列。这就是它实现发布/订阅模式的原理。"));

        questions.add(new QuizQuestion(5, "basic",
                "RabbitMQ 中的 Queue 声明为 durable 意味着什么？",
                List.of("消息不会丢失", "队列在 Broker 重启后仍然存在", "消费者断开后消息不会被删除", "队列会自动扩容"), 1,
                "durable=true 表示队列持久化，即 RabbitMQ 服务重启后队列本身不会丢失。但要注意，队列持久化不等于消息持久化，消息持久化需要单独设置 deliveryMode=2。"));

        // ===== 进阶题 =====
        questions.add(new QuizQuestion(6, "advanced",
                "Direct Exchange 和 Topic Exchange 的核心区别是什么？",
                List.of("Direct 支持多队列，Topic 不支持", "Direct 精确匹配 routing key，Topic 支持通配符匹配", "Direct 更快，Topic 更慢", "没有区别，只是名称不同"), 1,
                "Direct Exchange 要求 routing key 完全精确匹配，而 Topic Exchange 支持通配符：*（匹配一个单词）和 #（匹配零个或多个单词），实现更灵活的路由。"));

        questions.add(new QuizQuestion(7, "advanced",
                "Topic Exchange 中，routing key 为 'user.info.detail' 会被以下哪个绑定键匹配？",
                List.of("user.*", "user.#", "*.info.*", "以上都能匹配"), 3,
                "user.* 只匹配两个单词（如 user.create），不能匹配三个单词。user.# 匹配 user 开头的任意长度。*.info.* 匹配中间为 info 的三个单词。所以三个都能匹配。"));

        questions.add(new QuizQuestion(8, "advanced",
                "消息进入死信队列（DLQ）的触发条件不包括以下哪个？",
                List.of("消息被消费者拒绝（reject/nack）且 requeue=false", "消息 TTL 过期", "Exchange 不存在", "队列达到最大长度"), 2,
                "Exchange 不存在时消息会被直接丢弃或返回（如果设置了 mandatory），不会进入死信队列。进入死信队列的三个条件是：消息被拒绝、TTL 过期、队列满。"));

        questions.add(new QuizQuestion(9, "advanced",
                "使用 TTL + DLX 实现延迟队列时，存在什么问题？",
                List.of("消息可能丢失", "队头阻塞（Head-of-Line Blocking）", "不支持消息持久化", "延迟时间不准确"), 1,
                "TTL+DLX 方式按 FIFO 顺序处理，如果队列头部消息的 TTL 较长，后面 TTL 较短的消息即使已经过期也无法优先出队，造成队头阻塞。"));

        questions.add(new QuizQuestion(10, "advanced",
                "设置 prefetch=1 对工作队列有什么影响？",
                List.of("每个消费者每次只能预取一条消息", "队列最多存储一条消息", "生产者每次只能发送一条消息", "每秒只处理一条消息"), 0,
                "prefetch=1 表示消费者每次只预取一条消息，处理完当前消息后才会收到下一条。这实现了公平分发，让处理速度快的消费者获得更多消息。"));

        // ===== 高级题 =====
        questions.add(new QuizQuestion(11, "expert",
                "x-delayed-message 插件相比 TTL+DLX 方式的核心优势是什么？",
                List.of("消息更安全", "支持任意顺序的不同延迟时间，无队头阻塞", "不需要 Exchange", "性能更高"), 1,
                "x-delayed-message 插件将延迟消息暂存在 Exchange 中（基于 Mnesia），每条消息独立计时，到期后才投递到队列。不同延迟时间的消息不会相互阻塞。"));

        questions.add(new QuizQuestion(12, "expert",
                "RabbitMQ 的消息确认机制（ACK）中，auto-ack 模式的风险是什么？",
                List.of("消息发送过慢", "消费者处理失败时消息已被删除，造成消息丢失", "会导致消息重复", "无法使用死信队列"), 1,
                "auto-ack 模式下，消息一旦被投递给消费者就立即从队列中删除。如果消费者处理过程中崩溃，消息就会丢失。手动 ACK 模式下只有消费者显式确认后消息才会被删除。"));

        questions.add(new QuizQuestion(13, "expert",
                "在 Spring AMQP 中，@RabbitListener 注解的方法抛出异常后，消息默认会怎样？",
                List.of("直接丢弃", "重新入队（requeue）进行重试", "进入死信队列", "发送给其他消费者"), 1,
                "默认情况下，@RabbitListener 方法抛出异常后，消息会被 nack 并重新入队（requeue=true）。配合 retry 机制，达到最大重试次数后消息会被拒绝（requeue=false），如果配置了 DLX 则进入死信队列。"));

        questions.add(new QuizQuestion(14, "expert",
                "RabbitMQ 中 Exchange 的 durable 和 autoDelete 属性分别控制什么？",
                List.of("durable 控制消息持久化，autoDelete 控制消费者断开后删除", "durable 控制 Broker 重启后是否保留，autoDelete 控制最后一个绑定解除后是否删除", "durable 控制队列持久化，autoDelete 控制消息过期", "两者功能相同"), 1,
                "durable=true 表示 Exchange 在 RabbitMQ 重启后仍然存在。autoDelete=true 表示当最后一个绑定到该 Exchange 的队列解除绑定后，Exchange 会自动删除。"));

        questions.add(new QuizQuestion(15, "expert",
                "以下关于 RabbitMQ 消息可靠性保障的说法，哪个是错误的？",
                List.of("生产者确认（Publisher Confirm）可以确保消息到达 Broker", "消费者手动 ACK 可以避免消息丢失", "队列和消息都设为持久化即可保证 100% 不丢消息", "镜像队列可以提高高可用性"), 2,
                "即使队列和消息都设为持久化，在消息写入磁盘前 Broker 崩溃仍可能丢消息。要实现更高的可靠性，还需要配合 Publisher Confirm 和 Consumer ACK，以及镜像队列/仲裁队列。"));
    }
}
