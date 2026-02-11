-- 启用 PGVector 扩展
CREATE EXTENSION IF NOT EXISTS vector;

-- 创建向量存储表（如果不存在）
CREATE TABLE IF NOT EXISTS vector_store (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content TEXT,
    metadata JSON,
    embedding vector(1536)
);

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS vector_store_embedding_idx 
ON vector_store 
USING hnsw (embedding vector_cosine_ops);
