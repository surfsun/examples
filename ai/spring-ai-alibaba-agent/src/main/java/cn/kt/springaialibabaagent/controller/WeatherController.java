package cn.kt.springaialibabaagent.controller;

import cn.kt.springaialibabaagent.service.WeatherService;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping("/agent/chat")
    public AgentChatResponse chat(@RequestBody AgentChatRequest request) throws GraphRunnerException {
        String message = request == null ? null : request.message();
        String threadId = request == null ? null : request.threadId();
        String userId = request == null ? null : request.userId();
        List<ToolFeedbackRequest> feedbacks = request == null ? null : request.feedbacks();

        boolean hasFeedbacks = feedbacks != null && !feedbacks.isEmpty();
        if (!StringUtils.hasText(message) && !hasFeedbacks) {
            throw new IllegalArgumentException("message must not be blank");
        }
        if (hasFeedbacks && !StringUtils.hasText(threadId)) {
            throw new IllegalArgumentException("threadId must not be blank when sending feedbacks");
        }
        if (!StringUtils.hasText(threadId)) {
            threadId = UUID.randomUUID().toString();
        }
        if (!StringUtils.hasText(userId)) {
            userId = "1";
        }

        List<WeatherService.HumanFeedback> humanFeedbacks = toHumanFeedbacks(feedbacks);
        WeatherService.ChatResult result = weatherService.chat(message, threadId, userId, humanFeedbacks);
        return new AgentChatResponse(
                result.status().name(),
                result.threadId(),
                result.response(),
                result.pendingToolCalls());
    }

    private static List<WeatherService.HumanFeedback> toHumanFeedbacks(List<ToolFeedbackRequest> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return List.of();
        }
        List<WeatherService.HumanFeedback> results = new ArrayList<>();
        for (ToolFeedbackRequest feedback : feedbacks) {
            if (feedback == null) {
                continue;
            }
            WeatherService.FeedbackResult result = parseResult(feedback.result());
            results.add(new WeatherService.HumanFeedback(
                    feedback.toolCallId(),
                    feedback.toolName(),
                    result,
                    feedback.arguments(),
                    feedback.description()));
        }
        return results;
    }

    private static WeatherService.FeedbackResult parseResult(String result) {
        if (!StringUtils.hasText(result)) {
            return WeatherService.FeedbackResult.APPROVED;
        }
        return WeatherService.FeedbackResult.valueOf(result.trim().toUpperCase());
    }

    public record AgentChatRequest(String message,
                                   String threadId,
                                   String userId,
                                   List<ToolFeedbackRequest> feedbacks) {}

    public record ToolFeedbackRequest(String toolCallId,
                                      String toolName,
                                      String result,
                                      String arguments,
                                      String description) {}

    public record AgentChatResponse(String status,
                                    String threadId,
                                    String response,
                                    List<WeatherService.PendingToolCall> pendingToolCalls) {}
}
