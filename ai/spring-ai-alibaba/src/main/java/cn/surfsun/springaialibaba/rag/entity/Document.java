package cn.surfsun.springaialibaba.rag.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档实体类
 * 用于存储上传的文档元数据信息
 */
@Data
@Entity
@Table(name = "documents")
public class Document {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件名
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * 文档内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 上传时间
     */
    @Column(nullable = false)
    private LocalDateTime uploadTime;

    /**
     * 向量存储ID
     * 用于关联向量数据库中的文档
     */
    @Column(columnDefinition = "TEXT")
    private String vectorStoreIds;

    /**
     * 文档描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
    }
}
