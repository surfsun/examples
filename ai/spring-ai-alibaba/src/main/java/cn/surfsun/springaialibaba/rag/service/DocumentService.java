package cn.surfsun.springaialibaba.rag.service;

import cn.surfsun.springaialibaba.rag.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档服务接口
 */
public interface DocumentService {

    /**
     * 上传文档
     * 
     * @param file 上传的文件
     * @return 保存的文档实体
     */
    Document uploadDocument(MultipartFile file) throws Exception;

    /**
     * 获取所有文档列表
     * 
     * @return 文档列表
     */
    List<Document> getAllDocuments();

    /**
     * 根据ID获取文档
     * 
     * @param id 文档ID
     * @return 文档实体
     */
    Document getDocumentById(Long id);

    /**
     * 删除文档
     * 
     * @param id 文档ID
     */
    void deleteDocument(Long id);
}
