# MCP 教程（批发市场商品与库存案例）

本教程基于当前仓库中的实现，展示如何用 Spring AI MCP 构建：
1. MCP Server（暴露 Tools / Resources / Prompts）
2. MCP Client（在另一个 Spring Boot 项目中调用）
3. 三个演示接口的执行逻辑

> 项目路径：
> - Server：`/Users/kt/IdeaProjects/examples/ai/spring-ai-mcp-server`
> - Client：`/Users/kt/IdeaProjects/examples/ai/spring-ai-alibaba-agent`

---

## 1. MCP Server 实现概览

### 1.1 依赖与配置

Server 侧依赖：
- `spring-ai-starter-mcp-server-webmvc`：提供 MCP Server（SSE）
- `spring-ai-mcp-annotations`：提供注解扫描

配置文件（`src/main/resources/application.yaml`）：
- `spring.ai.mcp.server.protocol=SSE`
- `spring.ai.mcp.server.type=SYNC`
- `server.port=9001`

### 1.2 Mock 数据

数据由 `MockDataStore` 提供：
- 市场列表、品类列表、商品、供应商、库存批次

文件：
- `src/main/java/cn/kt/springaimcpserver/data/MockDataStore.java`

### 1.3 MCP Tools

`WholesaleMarketTools` 使用 `@McpTool` 定义工具：
- `search_products(keyword)`：查询商品
- `get_inventory(productId, marketId)`：查询库存
- `create_purchase_plan(productId, quantity)`：生成补货建议（mock）

文件：
- `src/main/java/cn/kt/springaimcpserver/mcp/WholesaleMarketTools.java`

### 1.4 MCP Resources

`WholesaleMarketResources` 使用 `@McpResource` 定义资源：
- `resource://markets`
- `resource://product-categories`
- `resource://supplier/{id}`

文件：
- `src/main/java/cn/kt/springaimcpserver/mcp/WholesaleMarketResources.java`

### 1.5 MCP Prompts

`WholesaleMarketPrompts` 使用 `@McpPrompt` 定义 prompt：
- `inventory-summary`（marketId）
- `price-analysis`（productId, days）

文件：
- `src/main/java/cn/kt/springaimcpserver/mcp/WholesaleMarketPrompts.java`

---

## 2. MCP Client 实现概览

### 2.1 Client 配置

Client 侧在 `spring-ai-alibaba-agent` 中通过代码创建 `McpSyncClient`：
- 使用 `HttpClientSseClientTransport`
- 连接 `http://localhost:9001/sse`

文件：
- `src/main/java/cn/kt/springaialibabaagent/mcp/McpClientConfig.java`

### 2.2 Demo Controller

Client 暴露 3 个演示接口：
- `/mcp/demo/overview`
- `/mcp/demo/run`
- `/mcp/demo/purchase-plan`

文件：
- `src/main/java/cn/kt/springaialibabaagent/mcp/McpDemoController.java`

---

## 3. 三个 Demo 接口的实现逻辑

### 3.1 `/mcp/demo/overview`

**目标**：查看 MCP Server 暴露的能力列表。  
**执行逻辑**：
1. `client.listTools()` → 读取 Tools
2. `client.listResources()` → 读取 Resources
3. `client.listPrompts()` → 读取 Prompts
4. 合并成 `OverviewResponse` 返回

> 结果能看到：
> - tools（工具名、入参 schema）
> - resources（资源 URI）
> - prompts（prompt 名与参数）

---

### 3.2 `/mcp/demo/run`

**目标**：演示一条完整链路：  
“搜索商品 → 查库存 → 读取资源 → 获取 prompt”

**执行逻辑**：
1. `search_products`  
   调用 `client.callTool(...)`，传入 `keyword`
2. 从 searchResult 中解析第一个 `productId`
3. `get_inventory`  
   使用第一个 `productId` + `marketId` 调用库存工具
4. `readResource(resource://markets)`  
   读取市场资源
5. `getPrompt(inventory-summary)`  
   获取库存摘要 prompt
6. 返回 `DemoRunResponse`

> 结果中：
> - `searchResult` 返回商品列表
> - `inventoryResult` 返回库存（若无商品则为 null）
> - `markets` 返回市场 JSON
> - `prompt` 返回可直接用于 LLM 的消息模板

---

### 3.3 `/mcp/demo/purchase-plan`

**目标**：调用补货建议工具。  
**执行逻辑**：
1. `client.callTool("create_purchase_plan", {productId, quantity})`
2. 返回 `CallToolResult`

> 结果中 `content` 为工具返回内容（JSON 文本）

---

## 4. 常见问题与排查

### 4.1 `@McpTool` 找不到包
注解包来自 `org.springaicommunity.mcp.annotation`，不是 `org.springframework.ai.mcp.annotation`。

### 4.2 Role.SYSTEM 报错
`McpSchema.Role` 只有 `USER` 和 `ASSISTANT`。提示词用 `ASSISTANT` 即可。

### 4.3 `inventoryResult` 为 null
当 `search_products` 返回为空时，没有可用 `productId`。

---

## 5. 下一步可扩展方向

1. Mock 数据替换为 ERP 实际接口  
2. 将 `resource://supplier/{id}` 与真实供应商服务打通  
3. 加入安全（token / API key）  
4. 切换为 Streamable-HTTP 或 Stateless MCP

---

## 6. 运行步骤（最简）

1. 启动 MCP Server（端口 9001）
2. 启动 Agent Client（端口 9000）
3. 访问：
   - `GET http://localhost:9000/mcp/demo/overview`
   - `GET http://localhost:9000/mcp/demo/run`
   - `GET http://localhost:9000/mcp/demo/purchase-plan?productId=prd-1001&quantity=120`

完成后即验证 MCP Server/Client 的 tools/resources/prompts 全链路可用。
