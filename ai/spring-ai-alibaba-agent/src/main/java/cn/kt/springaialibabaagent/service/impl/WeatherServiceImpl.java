package cn.kt.springaialibabaagent.service.impl;

import cn.kt.springaialibabaagent.service.WeatherService;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata;
import com.alibaba.cloud.ai.graph.action.InterruptionMetadata.ToolFeedback;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.SubGraphInterruptionException;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

@Service
public class WeatherServiceImpl implements WeatherService {

    private final ReactAgent agent;

    public WeatherServiceImpl(@Qualifier("weatherAgent") ReactAgent weatherAgent) {
        this.agent = weatherAgent;
    }

    @Override
    public ChatResult chat(String message, String threadId, String userId, List<HumanFeedback> feedbacks)
            throws GraphRunnerException {
        // 运行 agent
        RunnableConfig.Builder runnableConfigBuilder = RunnableConfig.builder()
                .threadId(threadId)
                .addMetadata("user_id", userId);

        if (feedbacks != null && !feedbacks.isEmpty()) {
            runnableConfigBuilder.addMetadata("HUMAN_FEEDBACK", toInterruptionMetadata(feedbacks));
        }

        String safeMessage = message == null ? "" : message;
        try {
            AssistantMessage response = agent.call(safeMessage, runnableConfigBuilder.build());
            if (response.hasToolCalls()) {
                List<PendingToolCall> pending = toPendingToolCalls(response);
                return new ChatResult(Status.NEED_HUMAN_APPROVAL, threadId, null, pending);
            }
            System.out.println("response: " + response.getText());
            return new ChatResult(Status.OK, threadId, response.getText(), List.of());
        } catch (SubGraphInterruptionException ex) {
            InterruptionMetadata interruptionMetadata = findInterruptionMetadata(ex.state());
            List<PendingToolCall> pendingToolCalls = toPendingToolCalls(interruptionMetadata);
            return new ChatResult(Status.NEED_HUMAN_APPROVAL, threadId, null, pendingToolCalls);
        }
    }

    private static InterruptionMetadata toInterruptionMetadata(List<HumanFeedback> feedbacks) {
        InterruptionMetadata.Builder builder = InterruptionMetadata.builder();
        for (HumanFeedback feedback : feedbacks) {
            if (feedback == null) {
                continue;
            }
            String toolCallId = feedback.toolCallId();
            String toolName = feedback.toolName();
            InterruptionMetadata.ToolFeedback.FeedbackResult result = toFeedbackResult(feedback.result());
            ToolFeedback toolFeedback = ToolFeedback.builder()
                    .id(toolCallId)
                    .name(toolName)
                    .arguments(feedback.arguments())
                    .description(feedback.description())
                    .result(result)
                    .build();
            builder.addToolFeedback(toolFeedback);
        }
        return builder.build();
    }

    private static InterruptionMetadata.ToolFeedback.FeedbackResult toFeedbackResult(WeatherService.FeedbackResult result) {
        if (result == null) {
            return InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED;
        }
        return switch (result) {
            case APPROVED -> InterruptionMetadata.ToolFeedback.FeedbackResult.APPROVED;
            case REJECTED -> InterruptionMetadata.ToolFeedback.FeedbackResult.REJECTED;
            case EDITED -> InterruptionMetadata.ToolFeedback.FeedbackResult.EDITED;
        };
    }

    private static List<PendingToolCall> toPendingToolCalls(InterruptionMetadata metadata) {
        if (metadata == null || metadata.toolFeedbacks() == null) {
            return List.of();
        }
        List<PendingToolCall> pending = new ArrayList<>();
        for (ToolFeedback feedback : metadata.toolFeedbacks()) {
            if (feedback == null) {
                continue;
            }
            pending.add(new PendingToolCall(
                    feedback.getId(),
                    feedback.getName(),
                    feedback.getArguments(),
                    feedback.getDescription()));
        }
        return pending;
    }

    private static List<PendingToolCall> toPendingToolCalls(AssistantMessage response) {
        if (response == null || !response.hasToolCalls()) {
            return List.of();
        }
        List<PendingToolCall> pending = new ArrayList<>();
        for (AssistantMessage.ToolCall toolCall : response.getToolCalls()) {
            if (toolCall == null) {
                continue;
            }
            pending.add(new PendingToolCall(
                    toolCall.id(),
                    toolCall.name(),
                    toolCall.arguments(),
                    "Tool call requires approval"));
        }
        return pending;
    }

    private static InterruptionMetadata findInterruptionMetadata(Map<String, Object> state) {
        if (state == null || state.isEmpty()) {
            return null;
        }
        Deque<Object> stack = new ArrayDeque<>();
        stack.push(state);
        while (!stack.isEmpty()) {
            Object current = stack.pop();
            switch (current) {
                case InterruptionMetadata metadata -> {
                    return metadata;
                }
                case Map<?, ?> map -> {
                    for (Object value : map.values()) {
                        if (value != null) {
                            stack.push(value);
                        }
                    }
                }
                case Iterable<?> iterable -> {
                    for (Object value : iterable) {
                        if (value != null) {
                            stack.push(value);
                        }
                    }
                }
                default -> {
                }
            }
        }
        return null;
    }
}
