package cn.surfsun.springaialibaba.rag.service.impl;


import cn.surfsun.springaialibaba.rag.entity.Document;
import cn.surfsun.springaialibaba.rag.repository.DocumentRepository;
import cn.surfsun.springaialibaba.rag.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档服务实现类
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;

    public DocumentServiceImpl(DocumentRepository documentRepository, VectorStore vectorStore) {
        this.documentRepository = documentRepository;
        this.vectorStore = vectorStore;
    }

    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file) throws Exception {
        log.info("开始上传文档: {}", file.getOriginalFilename());

        // 读取文件内容
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        // 创建文档实体
        Document document = new Document();
        document.setFileName(file.getOriginalFilename());
        document.setFileSize(file.getSize());
        document.setContent(content);

        // 保存到数据库
        document = documentRepository.save(document);

        try {
            // 创建文档读取器
            ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8)) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            DocumentReader reader = new TextReader(resource);
            List<org.springframework.ai.document.Document> documents = reader.get();

            // 文档分块
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<org.springframework.ai.document.Document> chunks = splitter.apply(documents);

            // 为每个分块添加文档ID元数据
            String docId = String.valueOf(document.getId());
            chunks.forEach(chunk -> {
                chunk.getMetadata().put("document_id", docId);
                chunk.getMetadata().put("file_name", file.getOriginalFilename());
            });

            // 存储到向量数据库
            vectorStore.add(chunks);

            // 保存向量存储ID
            String vectorIds = chunks.stream()
                    .map(org.springframework.ai.document.Document::getId)
                    .collect(Collectors.joining(","));
            document.setVectorStoreIds(vectorIds);
            document = documentRepository.save(document);

            log.info("文档上传成功，ID: {}, 分块数: {}", document.getId(), chunks.size());
        } catch (Exception e) {
            log.error("向量存储失败: {}", e.getMessage(), e);
            // 即使向量存储失败，文档元数据也已保存
            throw new Exception("文档向量化失败: " + e.getMessage(), e);
        }

        return document;
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAllByOrderByUploadTimeDesc();
    }

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文档不存在，ID: " + id));
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        Document document = getDocumentById(id);

        // 从向量数据库删除
        if (document.getVectorStoreIds() != null && !document.getVectorStoreIds().isEmpty()) {
            try {
                String[] ids = document.getVectorStoreIds().split(",");
                for (String vectorId : ids) {
                    vectorStore.delete(List.of(vectorId.trim()));
                }
                log.info("已从向量数据库删除文档，ID: {}", id);
            } catch (Exception e) {
                log.error("从向量数据库删除失败: {}", e.getMessage(), e);
            }
        }

        // 从数据库删除
        documentRepository.delete(document);
        log.info("文档删除成功，ID: {}", id);
    }
}
