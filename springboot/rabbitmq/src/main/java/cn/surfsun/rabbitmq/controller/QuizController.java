package cn.surfsun.rabbitmq.controller;

import cn.surfsun.rabbitmq.quiz.QuizQuestion;
import cn.surfsun.rabbitmq.quiz.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 知识问答 API
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    /** 按分类获取题目（basic / advanced / expert / all） */
    @GetMapping("/{category}")
    public ResponseEntity<List<QuizQuestion>> getQuestions(@PathVariable String category) {
        return ResponseEntity.ok(quizService.getByCategory(category));
    }

    /** 获取随机题目 */
    @GetMapping("/random/{count}")
    public ResponseEntity<List<QuizQuestion>> getRandomQuestions(@PathVariable int count) {
        return ResponseEntity.ok(quizService.getRandom(count));
    }

    /** 校验答案 */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAnswer(
            @RequestParam int questionId, @RequestParam int answer) {
        boolean correct = quizService.check(questionId, answer);
        return ResponseEntity.ok(Map.of("correct", correct, "questionId", questionId));
    }
}
