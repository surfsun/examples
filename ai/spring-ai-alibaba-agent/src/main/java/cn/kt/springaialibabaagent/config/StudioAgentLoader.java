package cn.kt.springaialibabaagent.config;

import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.agent.Agent;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudioAgentLoader implements AgentLoader {

    private final ReactAgent weatherAgent;

    public StudioAgentLoader(ReactAgent weatherAgent) {
        this.weatherAgent = weatherAgent;
    }

    @Override
    public List<String> listAgents() {
        return List.of(AgentConfig.STUDIO_DEFAULT_AGENT_NAME, AgentConfig.WEATHER_AGENT_NAME);
    }

    @Override
    public Agent loadAgent(String name) {
        if (AgentConfig.STUDIO_DEFAULT_AGENT_NAME.equals(name)
                || AgentConfig.WEATHER_AGENT_NAME.equals(name)) {
            return weatherAgent;
        }
        throw new IllegalArgumentException("Unknown agent: " + name);
    }
}
