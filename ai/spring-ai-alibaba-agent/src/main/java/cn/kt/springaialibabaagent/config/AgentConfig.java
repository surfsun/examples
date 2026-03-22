package cn.kt.springaialibabaagent.config;

import cn.kt.springaialibabaagent.tool.UserLocationTool;
import cn.kt.springaialibabaagent.tool.WeatherTool;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.Hook;
import com.alibaba.cloud.ai.graph.agent.hook.hip.HumanInTheLoopHook;
import com.alibaba.cloud.ai.graph.agent.hook.hip.ToolConfig;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    public static final String WEATHER_AGENT_NAME = "weather_agent";
    public static final String STUDIO_DEFAULT_AGENT_NAME = "research_agent";

    private static final String SYSTEM_PROMPT = """
            You are an expert weather assistant.
            You have access to two tools:
            - get_weather_for_location: get weather for a specific location
            - get_user_location: get the user's default location if they did not specify one

            If a user asks about weather, make sure you have a location.
            If the location is missing or ambiguous, call get_user_location first.
            """;

    @Bean
    public ToolCallback weatherTool() {
        return FunctionToolCallback.builder("get_weather_for_location", new WeatherTool())
                .description("Get weather for a given city or location name")
                .inputType(String.class)
                .build();
    }

    @Bean
    public ToolCallback userLocationTool() {
        return FunctionToolCallback.builder("get_user_location", new UserLocationTool())
                .description("Retrieve user location based on user ID")
                .inputType(String.class)
                .build();
    }

    @Bean
    public Hook humanInTheLoopHook() {
        return HumanInTheLoopHook.builder()
                .approvalOn("get_weather_for_location",
                        ToolConfig.builder().description("Please confirm tool execution.").build())
                .build();
    }

    @Bean
    public ReactAgent weatherAgent(ChatModel chatModel,
                                   ToolCallback weatherTool,
                                   ToolCallback userLocationTool,
                                   Hook humanInTheLoopHook) {
        return ReactAgent.builder()
                .name(STUDIO_DEFAULT_AGENT_NAME)
                .model(chatModel)
                .tools(userLocationTool, weatherTool)
                .systemPrompt(SYSTEM_PROMPT)
                .hooks(humanInTheLoopHook)
                .saver(new MemorySaver())
                .build();
    }
}
