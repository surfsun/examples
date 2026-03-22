package cn.kt.springaimcpserver.mcp;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpPrompt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WholesaleMarketPrompts {

    @McpPrompt(name = "inventory-summary", description = "Prompt for inventory summary by market")
    public GetPromptResult inventorySummary(
            @McpArg(name = "marketId", description = "Market ID", required = true) String marketId) {
        String message = """
                You are an inventory analyst for a wholesale market.
                Summarize today's inventory for market %s.
                Use the get_inventory tool for key products and highlight low-stock items.
                """.formatted(marketId);
        return new GetPromptResult(
                "Inventory Summary",
                List.of(new PromptMessage(Role.ASSISTANT, new TextContent(message)))
        );
    }

    @McpPrompt(name = "price-analysis", description = "Prompt for price trend analysis")
    public GetPromptResult priceAnalysis(
            @McpArg(name = "productId", description = "Product ID", required = true) String productId,
            @McpArg(name = "days", description = "Number of days", required = true) int days) {
        String message = """
                You are a pricing analyst.
                Analyze price trend for product %s over the last %d days.
                If the price data is missing, explain assumptions clearly.
                """.formatted(productId, days);
        return new GetPromptResult(
                "Price Analysis",
                List.of(new PromptMessage(Role.ASSISTANT, new TextContent(message)))
        );
    }
}
