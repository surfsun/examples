package cn.surfsun.springaialibaba.simpleHttp.service.impl;

import cn.surfsun.springaialibaba.simpleHttp.service.SimpleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class SimpleServiceImpl implements SimpleService {

    private final RestClient restClient;
    @Value("${custom.keys.authorization:''}")
    private String authorization;

    public SimpleServiceImpl(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1").build();
    }

    @Override
    public String chat(String message) {
        Map<String, Object> requestBody = Map.of(
                "model", "deepseek-v3.2", // Using the model from the http file example or qwen-turbo/plus
                "messages", List.of(Map.of("role", "user", "content", message)),
                "stream", false);

        // Ideally we should map the response to a class, but for simplicity returning
        // String or map first to see structure
        // The user asked for "call large model interface".

        return restClient.post()
                .uri("/chat/completions")
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }
}
