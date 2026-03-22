package cn.kt.springaialibabaagent.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Configuration
public class ErpAgentConfig {

    public static final String ERP_AGENT_NAME = "erp_agent";
    private static final Logger log = LoggerFactory.getLogger(ErpAgentConfig.class);

    private static final String ERP_SYSTEM_PROMPT = """
            You are an ERP Copilot for agricultural wholesale markets.
            Your goal is to help users operate the ERP efficiently and accurately.

            Core responsibilities:
            1) Inventory & turnover: check stock, batches, low-stock risks, slow-moving items.
            2) Procurement: suggest purchase plans based on current stock and demand.
            3) Supplier info: find supplier details by ID.
            4) Market info: list markets and categories when needed.

            Tool usage rules:
            - Use MCP tools for any factual data (products, inventory, suppliers, markets, categories).
            - Do not fabricate IDs or quantities. If required inputs are missing, ask a concise question.
            - If a tool returns empty, explain that no data was found and suggest next steps.

            Response style:
            - Be concise and actionable.
            - Prefer short bullet lists.
            - Provide clear next steps when appropriate.
    """;

    @Bean
    @Lazy
    public ReactAgent erpAgent(ChatModel chatModel,
                               McpSyncClient wholesaleMcpClient,
                               cn.kt.springaialibabaagent.mcp.McpAvailabilityChecker mcpAvailabilityChecker) {
        if (!mcpAvailabilityChecker.isAvailable()) {
            return erpFallbackAgent(chatModel);
        }
        List<ToolCallback> toolCallbacks;
        try {
            wholesaleMcpClient.initialize();
            toolCallbacks = SyncMcpToolCallbackProvider.syncToolCallbacks(List.of(wholesaleMcpClient));
        } catch (Exception ex) {
            log.warn("MCP tools unavailable, falling back to ERP help mode.", ex);
            return erpFallbackAgent(chatModel);
        }
        return ReactAgent.builder()
                .name(ERP_AGENT_NAME)
                .model(chatModel)
                .tools(toolCallbacks.toArray(new ToolCallback[0]))
                .systemPrompt(ERP_SYSTEM_PROMPT)
                .saver(new MemorySaver())
                .build();
    }

    @Bean
    public ReactAgent erpFallbackAgent(ChatModel chatModel) {
        String fallbackPrompt = """
                The ERP MCP service is currently unavailable.
                Please start the MCP server (default: http://localhost:9001) and try again.

                Meanwhile, I can explain how to use the ERP features or answer general questions.
                """;
        return ReactAgent.builder()
                .name(ERP_AGENT_NAME + "_fallback")
                .model(chatModel)
                .systemPrompt(fallbackPrompt)
                .saver(new MemorySaver())
                .build();
    }
}
