package cn.surfsun.springaialibaba.rag.service;

/**
 * RAG服务接口
 */
public interface RagService {

    /**
     * 简单对话（不使用RAG）
     */
    String chat(String message);

    /**
     * RAG对话（使用向量检索）
     * 
     * @param message   用户消息
     * @param sessionId 会话ID，用于区分不同用户的聊天历史
     * @return AI回复
     */
    String chatWithRag(String message, String sessionId);
}
