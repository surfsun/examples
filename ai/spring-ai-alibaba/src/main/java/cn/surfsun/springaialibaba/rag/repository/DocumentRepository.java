package cn.surfsun.springaialibaba.rag.repository;

import cn.surfsun.springaialibaba.rag.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文档数据访问接口
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * 根据文件名查询文档
     */
    List<Document> findByFileName(String fileName);

    /**
     * 按上传时间倒序查询所有文档
     */
    List<Document> findAllByOrderByUploadTimeDesc();
}
