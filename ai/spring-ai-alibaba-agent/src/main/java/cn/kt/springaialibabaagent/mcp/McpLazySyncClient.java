package cn.kt.springaialibabaagent.mcp;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.GetPromptRequest;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.ListPromptsResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourcesResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourceTemplatesResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.SubscribeRequest;
import io.modelcontextprotocol.spec.McpSchema.UnsubscribeRequest;

import java.util.concurrent.atomic.AtomicBoolean;

public class McpLazySyncClient implements AutoCloseable {

    private final McpSyncClient delegate;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public McpLazySyncClient(McpSyncClient delegate) {
        this.delegate = delegate;
    }

    public ListToolsResult listTools() {
        ensureInitialized();
        return delegate.listTools();
    }

    public ListResourcesResult listResources() {
        ensureInitialized();
        return delegate.listResources();
    }

    public ListResourceTemplatesResult listResourceTemplates() {
        ensureInitialized();
        return delegate.listResourceTemplates();
    }

    public ReadResourceResult readResource(ReadResourceRequest request) {
        ensureInitialized();
        return delegate.readResource(request);
    }

    public ListPromptsResult listPrompts() {
        ensureInitialized();
        return delegate.listPrompts();
    }

    public GetPromptResult getPrompt(GetPromptRequest request) {
        ensureInitialized();
        return delegate.getPrompt(request);
    }

    public CallToolResult callTool(CallToolRequest request) {
        ensureInitialized();
        return delegate.callTool(request);
    }

    public void subscribeResource(SubscribeRequest request) {
        ensureInitialized();
        delegate.subscribeResource(request);
    }

    public void unsubscribeResource(UnsubscribeRequest request) {
        ensureInitialized();
        delegate.unsubscribeResource(request);
    }

    private void ensureInitialized() {
        if (initialized.compareAndSet(false, true)) {
            delegate.initialize();
        }
    }

    @Override
    public void close() {
        delegate.close();
    }
}
