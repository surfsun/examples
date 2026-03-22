package cn.kt.springaialibabaagent.mcp;

import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.GetPromptRequest;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.ListPromptsResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourcesResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp/demo")
public class McpDemoController {

    private final McpLazySyncClient client;

    public McpDemoController(@Lazy McpLazySyncClient client) {
        this.client = client;
    }

    @GetMapping("/overview")
    public OverviewResponse overview() {
        ListToolsResult tools = client.listTools();
        ListResourcesResult resources = client.listResources();
        ListPromptsResult prompts = client.listPrompts();
        return new OverviewResponse(tools, resources, prompts);
    }

    @GetMapping("/run")
    public DemoRunResponse run(@RequestParam(defaultValue = "苹果") String keyword,
                               @RequestParam(defaultValue = "mkt-1") String marketId) {
        CallToolResult searchResult = client.callTool(new CallToolRequest(
                "search_products",
                Map.of("keyword", keyword)
        ));

        String productId = extractFirstProductId(searchResult);
        CallToolResult inventoryResult = productId == null
                ? null
                : client.callTool(new CallToolRequest(
                "get_inventory",
                Map.of("productId", productId, "marketId", marketId)
        ));

        ReadResourceResult markets = client.readResource(new ReadResourceRequest("resource://markets"));
        GetPromptResult prompt = client.getPrompt(new GetPromptRequest(
                "inventory-summary",
                Map.of("marketId", marketId)
        ));

        return new DemoRunResponse(searchResult, inventoryResult, markets, prompt);
    }

    @GetMapping("/purchase-plan")
    public CallToolResult purchasePlan(@RequestParam String productId,
                                       @RequestParam(defaultValue = "120") int quantity) {
        return client.callTool(new CallToolRequest(
                "create_purchase_plan",
                Map.of("productId", productId, "quantity", quantity)
        ));
    }

    private static String extractFirstProductId(CallToolResult searchResult) {
        if (searchResult == null || searchResult.structuredContent() == null) {
            return null;
        }
        Object structured = searchResult.structuredContent();
        if (structured instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?, ?> map) {
                Object id = map.get("id");
                return id == null ? null : id.toString();
            }
        }
        if (structured instanceof Map<?, ?> map) {
            Object id = map.get("id");
            return id == null ? null : id.toString();
        }
        return null;
    }

    public record OverviewResponse(ListToolsResult tools,
                                   ListResourcesResult resources,
                                   ListPromptsResult prompts) {}

    public record DemoRunResponse(CallToolResult searchResult,
                                  CallToolResult inventoryResult,
                                  ReadResourceResult markets,
                                  GetPromptResult prompt) {}
}
