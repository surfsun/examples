package cn.kt.springaialibabaagent.mcp;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class McpAvailabilityChecker {

    @Value("${app.mcp.server-url}")
    private String serverUrl;

    private URI serverUri;
    private final int timeoutMillis;
    private final long cacheTtlMillis;
    private final AtomicLong lastCheckedAt = new AtomicLong(0);
    private final AtomicReference<Boolean> lastResult = new AtomicReference<>(null);

    public McpAvailabilityChecker() {
        this(Duration.ofMillis(300), Duration.ofSeconds(3));
    }

    McpAvailabilityChecker(Duration timeout, Duration cacheTtl) {
        this.timeoutMillis = Math.toIntExact(timeout.toMillis());
        this.cacheTtlMillis = cacheTtl.toMillis();
    }

    @PostConstruct
    void init() {
        if (serverUrl == null || serverUrl.isBlank()) {
            throw new IllegalArgumentException("app.mcp.server-url must be set");
        }
        try {
            this.serverUri = new URI(serverUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid MCP server URL: " + serverUrl, e);
        }
    }

    public boolean isAvailable() {
        long now = System.currentTimeMillis();
        long last = lastCheckedAt.get();
        Boolean cached = lastResult.get();
        if (cached != null && (now - last) < cacheTtlMillis) {
            return cached;
        }
        boolean result = probe();
        lastCheckedAt.set(now);
        lastResult.set(result);
        return result;
    }

    private boolean probe() {
        String host = serverUri.getHost();
        if (host == null || host.isBlank()) {
            return false;
        }
        int port = serverUri.getPort();
        if (port <= 0) {
            port = "https".equalsIgnoreCase(serverUri.getScheme()) ? 443 : 80;
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMillis);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
