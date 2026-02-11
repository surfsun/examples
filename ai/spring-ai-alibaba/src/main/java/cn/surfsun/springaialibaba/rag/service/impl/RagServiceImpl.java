package cn.surfsun.springaialibaba.rag.service.impl;

import cn.surfsun.springaialibaba.rag.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * RAG服务实现类
 */
@Slf4j
@Service
public class RagServiceImpl implements RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    // RAG 系统提示词
    private static final String RAG_SYSTEM_PROMPT = """
            你是一个文档助手。请优先使用提供的上下文信息来回答用户的问题。
            
            上下文信息如下：
            ---------------------
            {context}
            ---------------------
            
            如果上下文信息中没有相关内容，请在回答的开头加上："[提醒：文档库中没有找到相关资料，以下是基于我的通用知识为您提供的回答]"，然后根据你的知识尽可能全面地回答用户。
            """;

    public RagServiceImpl(ChatClient.Builder chatClientBuilder, VectorStore vectorStore, ChatMemory chatMemory) {
        this.vectorStore = vectorStore;
        // 构建带有记忆能力的 ChatClient
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Override
    public String chat(String message) {
        log.info("简单对话: {}", message);
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    @Override
    public String chatWithRag(String message, String sessionId) {
        log.info("RAG对话 [Session: {}]: {}", sessionId, message);

        try {
            // 1. 从向量数据库检索相关文档
            SearchRequest request = SearchRequest.builder()
                    .query(message)
                    .topK(5)
                    .similarityThreshold(0.5) // 可选：设置相似度阈值
                    .build();

            List<Document> similarDocuments = vectorStore.similaritySearch(request);
            log.info("RAG检索 {} 个相关文档块", similarDocuments.size());

            String context;
            boolean foundRelevant = !similarDocuments.isEmpty();

            if (foundRelevant) {
                // 构建检索到的上下文
                context = similarDocuments.stream()
                        .map(doc -> {
                            String fileName = (String) doc.getMetadata().get("file_name");
                            return String.format("[来源: %s]\n%s", fileName, doc.getText());
                        })
                        .collect(Collectors.joining("\n\n---\n\n"));
            } else {
                // 未找到相关内容
                log.info("未找到相关文档，将使用大模型通用知识回答");
                context = "没有检索到相关的本地文档内容。";
            }

            log.info("RAG检索 context{}", context);
            // 2. 调用大模型（带记忆和上下文）
            return chatClient.prompt()
                    .advisors(advisorSpec -> advisorSpec
                            .param(CONVERSATION_ID, sessionId)
                            .param("chat_memory_retrieve_size", 10))
                    .system(s -> s.text(RAG_SYSTEM_PROMPT).param("context", context))
                    .user(message)
                    .call()
                    .content();

        } catch (Exception e) {
            log.error("RAG对话失败: {}", e.getMessage(), e);
            // 失败时也尝试直接用模型回答，不带 RAG
            return chatClient.prompt()
                    .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, sessionId))
                    .user(message)
                    .call()
                    .content();
        }
    }
}
