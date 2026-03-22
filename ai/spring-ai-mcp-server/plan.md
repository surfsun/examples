# 需求
spring ai mcp的教程地址有：
https://java2ai.com/integration/mcps/mcp-overview
https://java2ai.com/integration/mcps/mcp-helpers
https://java2ai.com/integration/mcps/mcp-security
https://java2ai.com/integration/mcps/mcp-client-boot-starter-docs
https://java2ai.com/integration/mcps/mcp-server-boot-starter-docs
https://java2ai.com/integration/mcps/mcp-stateless-server-boot-starter-docs
https://java2ai.com/integration/mcps/mcp-stdio-sse-server-boot-starter-docs
https://java2ai.com/integration/mcps/mcp-streamable-http-server-boot-starter-docs

https://java2ai.com/integration/mcps/annotations/mcp-annotations-overview
https://java2ai.com/integration/mcps/annotations/mcp-annotations-server
https://java2ai.com/integration/mcps/annotations/mcp-annotations-client

https://java2ai.com/integration/mcps/examples/mcp-server
https://java2ai.com/integration/mcps/examples/mcp-client

根据这些教程，设计一个mcp server和mcp client的例子，用 “批发市场商品与库存 MCP 服务”做为这个mcp的案例。
先在/Users/kt/IdeaProjects/examples/ai/spring-ai-mcp-server/plan.md做好 详细的规划，后续再根据这个文档进行编码。

server的工程是/Users/kt/IdeaProjects/examples/ai/spring-ai-mcp-server
client的工程是准备在/Users/kt/IdeaProjects/examples/ai/spring-ai-alibaba-agent中使用。

spring-ai-mcp-server的maven依赖，可以参考spring-ai-alibaba-agent已经可以用的方案做

# 计划
## 目标
- 在 `spring-ai-mcp-server` 中实现一个 MCP Server（批发市场商品与库存服务）。
- 在 `spring-ai-alibaba-agent` 中实现 MCP Client 调用示例。
- 覆盖 MCP 的核心能力：Tools、Resources、Prompts、传输协议（优先 SSE）、基础安全/鉴权（可选）。

## 方案范围
- Server：`/Users/kt/IdeaProjects/examples/ai/spring-ai-mcp-server`
- Client：`/Users/kt/IdeaProjects/examples/ai/spring-ai-alibaba-agent`
- 业务案例：批发市场商品与库存 MCP 服务

## Server 设计
### 传输协议
- 默认：WebMVC + SSE（方便调试和演示）
- 可切换：Streamable-HTTP / Stateless（预留配置）

### MCP 能力覆盖
1. Tools
   - `search_products(keyword)`：按关键字检索商品（返回 ID/名称/规格/品类）
   - `get_inventory(productId, marketId)`：查询指定市场库存与批次
   - `create_purchase_plan(productId, quantity)`：生成补货建议（可 mock）
2. Resources
   - `resource://markets`：市场列表
   - `resource://product-categories`：品类树
   - `resource://supplier/{id}`：供应商信息
3. Prompts
   - `prompt://inventory-summary`：生成库存摘要的 prompt（参数：marketId）
   - `prompt://price-analysis`：价格趋势分析的 prompt（参数：productId, days）

### 数据源
- 初期使用内存数据或 JSON 文件（可热替换）
- 预留接口层，后续对接 ERP 实际 API

### 目录与模块建议
- `config/`：MCP Server 配置（protocol/transport）
- `mcp/`：MCP 注解入口（Tools/Resources/Prompts）
- `data/`：内存数据或 JSON 文件加载器
- `service/`：业务服务层（商品、库存、供应商）

## Client 设计（在 spring-ai-alibaba-agent 中）
### 目标
- 通过 MCP Client 调用 Server 工具、资源、prompt
- 提供简单 API 或 CLI 演示调用

### 调用示例
1. 列出工具列表
2. 调用 `search_products`，拿到产品 ID
3. 调用 `get_inventory`，输出库存
4. 读取 `resource://markets`
5. 调用 `prompt://inventory-summary` 并组合模型调用（可选）

### 传输
- 使用与 Server 对应的 SSE/Streamable-HTTP Client Starter

## 配置清单（预期）
### Server
- 依赖：MCP Server Starter（WebMVC SSE）
- 配置：`spring.ai.mcp.server.protocol=SSE`
- 端口：默认 9001（避免与现有 9000 冲突）

### Client
- 依赖：MCP Client Starter（Servlet 或 WebFlux）
- 配置：MCP Server URL

## 开发步骤
1. Server
   - 添加 MCP Server 依赖与配置
   - 实现 MCP Tools/Resources/Prompts（注解方式）
   - 准备内存数据模型与服务层
   - 启动并通过 MCP 客户端或 curl 验证
2. Client
   - 添加 MCP Client 依赖与配置
   - 编写调用示例（Controller 或 CommandLineRunner）
   - 验证与 Server 联通
3. 文档与演示
   - 提供调用流程与示例请求
   - 说明如何切换协议（SSE/Streamable）

## 验收标准
- Server 正常启动并暴露 MCP 接口
- Client 能成功发现并调用 tools/resources/prompts
- 示例链路可复现：搜索商品 -> 查库存 -> 生成摘要


## 如何运行

1. 启动 MCP Server（端口 9001）
2. 启动 Agent 服务（端口 9000）

然后访问：

- GET http://localhost:9000/mcp/demo/overview
- GET http://localhost:9000/mcp/demo/run
- GET http://localhost:9000/mcp/demo/purchase-plan?productId=prd-1001&quantity=120