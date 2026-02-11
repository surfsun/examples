-- 修改 documents 表的 vector_store_ids 列类型
-- 从 VARCHAR(500) 改为 TEXT，以支持大文件的多个向量ID存储

ALTER TABLE documents 
ALTER COLUMN vector_store_ids TYPE TEXT;

-- 可选：同时修改 description 列为 TEXT，以支持更长的描述
ALTER TABLE documents 
ALTER COLUMN description TYPE TEXT;
