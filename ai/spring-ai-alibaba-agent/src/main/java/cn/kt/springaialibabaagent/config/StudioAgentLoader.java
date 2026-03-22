package cn.kt.springaialibabaagent.config;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudioAgentLoader implements AgentLoader {

    private final ReactAgent weatherAgent;
    private final ObjectProvider<ReactAgent> erpAgentProvider;
    private final ReactAgent erpFallbackAgent;
    private final cn.kt.springaialibabaagent.mcp.McpAvailabilityChecker mcpAvailabilityChecker;

    public StudioAgentLoader(@Qualifier("weatherAgent") ReactAgent weatherAgent,
                             @Qualifier("erpAgent") ObjectProvider<ReactAgent> erpAgentProvider,
                             @Qualifier("erpFallbackAgent") ReactAgent erpFallbackAgent,
                             cn.kt.springaialibabaagent.mcp.McpAvailabilityChecker mcpAvailabilityChecker) {
        this.weatherAgent = weatherAgent;
        this.erpAgentProvider = erpAgentProvider;
        this.erpFallbackAgent = erpFallbackAgent;
        this.mcpAvailabilityChecker = mcpAvailabilityChecker;
    }

    @NonNull
    @Override
    public List<String> listAgents() {
        return List.of(
                AgentConfig.STUDIO_DEFAULT_AGENT_NAME,
                AgentConfig.WEATHER_AGENT_NAME,
                ErpAgentConfig.ERP_AGENT_NAME
        );
    }

    @NonNull
    @Override
    public Agent loadAgent(@NonNull String name) {
        if (AgentConfig.STUDIO_DEFAULT_AGENT_NAME.equals(name)) {
            return selectErpAgent();
        }
        if (AgentConfig.WEATHER_AGENT_NAME.equals(name)) {
            return weatherAgent;
        }
        if (ErpAgentConfig.ERP_AGENT_NAME.equals(name)) {
            return selectErpAgent();
        }
        throw new IllegalArgumentException("Unknown agent: " + name);
    }

    private Agent selectErpAgent() {
        if (mcpAvailabilityChecker.isAvailable()) {
            return erpAgentProvider.getObject();
        }
        return erpFallbackAgent;
    }
}
