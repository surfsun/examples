package cn.surfsun.rabbitmq.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 消息日志实体
 * 用于记录消息的发送和消费过程，通过 SSE 推送到前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageLog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** 消息所属的模式，如 simple、work、fanout 等 */
    private String pattern;

    /** 日志方向：SENT（已发送）/ RECEIVED（已接收） */
    private String direction;

    /** 来源标识，如 "Producer"、"Consumer-1" */
    private String source;

    /** 消息内容 */
    private String content;

    /** 时间戳 */
    private String timestamp;

    public static MessageLog sent(String pattern, String content) {
        return new MessageLog(pattern, "SENT", "Producer", content, LocalDateTime.now().format(FMT));
    }

    public static MessageLog received(String pattern, String source, String content) {
        return new MessageLog(pattern, "RECEIVED", source, content, LocalDateTime.now().format(FMT));
    }
}
