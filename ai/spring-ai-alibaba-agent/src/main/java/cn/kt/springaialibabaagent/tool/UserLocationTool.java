package cn.kt.springaialibabaagent.tool;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.tools.ToolContextConstants;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Optional;
import java.util.function.BiFunction;

public class UserLocationTool implements BiFunction<String, ToolContext, String> {

    @Override
    public String apply(@ToolParam(description = "User query") String query, ToolContext toolContext) {
        String userId = "1";
        if (toolContext != null && toolContext.getContext() != null) {
            Object runnableConfigObj = toolContext.getContext().get(ToolContextConstants.AGENT_CONFIG_CONTEXT_KEY);
            if (runnableConfigObj instanceof RunnableConfig runnableConfig) {
                Optional<Object> userIdObj = runnableConfig.metadata("user_id");
                if (userIdObj.isPresent()) {
                    userId = String.valueOf(userIdObj.get());
                }
            }
        }

        return switch (userId) {
            case "2" -> "Shanghai";
            case "3" -> "Beijing";
            default -> "Chengdu";
        };
    }
}
