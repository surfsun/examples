package cn.kt.springaialibabaagent.service;

import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;

import java.util.List;

public interface WeatherService {

    ChatResult chat(String message, String threadId, String userId, List<HumanFeedback> feedbacks) throws GraphRunnerException;

    record ChatResult(Status status, String threadId, String response, List<PendingToolCall> pendingToolCalls) {}

    record PendingToolCall(String toolCallId, String toolName, String arguments, String description) {}

    record HumanFeedback(String toolCallId, String toolName, FeedbackResult result, String arguments, String description) {}

    enum Status {
        OK,
        NEED_HUMAN_APPROVAL
    }

    enum FeedbackResult {
        APPROVED,
        REJECTED,
        EDITED
    }
}
