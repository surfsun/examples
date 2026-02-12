package cn.surfsun.rabbitmq.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识问答题目实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestion {
    private int id;
    private String category;    // 分类：basic / advanced / expert
    private String question;    // 题目
    private List<String> options; // 选项
    private int answer;         // 正确答案索引 (0-based)
    private String explanation; // 答案解析
}
