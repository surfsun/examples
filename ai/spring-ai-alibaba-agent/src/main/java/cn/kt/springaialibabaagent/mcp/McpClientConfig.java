package cn.kt.springaialibabaagent.mcp;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;

@Configuration
public class McpClientConfig {

    @Bean(destroyMethod = "close")
    @Lazy
    public McpSyncClient wholesaleMcpClient(@Value("${app.mcp.server-url}") String baseUrl) {
        var transport = HttpClientSseClientTransport.builder(baseUrl)
                .sseEndpoint("/sse")
                .jsonMapper(McpJsonMapper.createDefault())
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        return McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Bean(destroyMethod = "close")
    @Lazy
    public McpLazySyncClient wholesaleMcpLazyClient(McpSyncClient wholesaleMcpClient) {
        return new McpLazySyncClient(wholesaleMcpClient);
    }
}
