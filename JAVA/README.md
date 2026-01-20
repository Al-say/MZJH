# 多智能体流式圆桌讨论系统

这是一个基于Spring Boot的多智能体流式圆桌讨论系统，支持5个智能体席位的实时流式讨论。

## 功能特性

- **固定5席位讨论**: 每次讨论严格限制为5个智能体席位
- **数据库驱动配置**: 席位和模板通过PostgreSQL数据库管理，支持热更新
- **实时WebSocket通信**: 使用STOMP协议进行实时流式消息传递
- **模板系统**: 支持预定义的席位组合模板
- **RESTful管理API**: 提供HTTP API进行席位和模板的CRUD操作
- **流式响应**: 支持智能体的流式文本输出

## 技术栈

- **后端**: Java 17, Spring Boot 3.x
- **数据库**: PostgreSQL
- **缓存**: Redis
- **通信**: WebSocket STOMP
- **ORM**: Spring Data JPA
- **构建工具**: Maven

## 项目结构

```
src/main/java/com/alsay/mzjh/
├── controller/          # REST控制器
├── entity/             # JPA实体
├── orchestrator/       # 业务逻辑编排
├── protocol/           # 通信协议模型
├── repository/         # 数据访问层
├── delivery/           # 消息传递服务
└── llm/                # LLM适配器接口
```

## API接口

### 席位管理
- `GET /api/agent-seats` - 获取所有启用的席位
- `POST /api/agent-seats` - 创建新席位
- `PUT /api/agent-seats/{id}` - 更新席位
- `DELETE /api/agent-seats/{id}` - 删除席位（软删除）

### 模板管理
- `GET /api/templates` - 获取所有启用的模板
- `POST /api/templates` - 创建新模板
- `PUT /api/templates/{id}` - 更新模板
- `DELETE /api/templates/{id}` - 删除模板（软删除）

### 主题管理
- `POST /api/topics` - 创建新讨论主题

### WebSocket通信
- **端点**: `/roundtable-ws`
- **控制频道**: `/topic/roundtable/control` (系统事件)
- **专属频道**: `/topic/roundtable/{topicId}` (席位事件)
- **发送**: `/app/startDiscussion`, `/app/cancelDiscussion`

## 快速开始

1. 确保安装了Java 17和PostgreSQL
2. 创建PostgreSQL数据库
3. 配置`application.yml`中的数据库连接
4. 运行应用: `mvn spring-boot:run`
5. 访问管理API或连接WebSocket进行讨论

## 配置说明

应用默认运行在端口22222，WebSocket端点为`/roundtable-ws`。

数据库配置在`application.yml`中，支持PostgreSQL连接。

## 讨论流程

1. 客户端调用 `POST /api/topics` 创建讨论主题，获得 `topicId`
2. 客户端订阅 `/topic/roundtable/{topicId}` 专属频道
3. 客户端通过WebSocket发送`ClientMessage`，包含议题和席位选择
4. 系统验证并解析5个席位
5. 并发启动5个智能体的流式响应
6. 实时推送消息到专属频道 `/topic/roundtable/{topicId}`
## 事件协议说明

### topicId 获取方式与时序

**方案选择**: 前端先通过HTTP请求获取`topicId`，再用此ID发起STOMP讨论。

**具体流程**:
1. 前端调用 `POST /api/topics` 创建讨论主题，获得 `topicId`
2. 前端订阅 `/topic/roundtable/{topicId}` 频道
3. 前端发送 `/app/startDiscussion` 消息，包含获取的 `topicId`
4. 后端验证 `topicId` 并开始推送事件到对应频道

**HTTP API**:
```http
POST /api/topics
Content-Type: application/json

{
  "topic": "讨论议题内容"
}
```

**响应**:
```json
{
  "topicId": "uuid-string",
  "createdAt": "2024-01-20T10:00:00Z"
}
```

### 客户端消息 (ClientMessage)
客户端通过STOMP发送的消息格式：

```json
{
  "action": "START|CANCEL",
  "topicId": "string",
  "topic": "string",           // 讨论议题 (START时必需)
  "templateId": "long",        // 模板ID (START时可选)
  "seatIds": ["long", ...]     // 席位ID列表 (START时可选, 最多5个)
}
```

- `action`: 操作类型，"START"开始讨论，"CANCEL"取消讨论
- `topicId`: 讨论主题ID，用于标识特定讨论
- `topic`: 讨论议题内容
- `templateId`: 模板ID，用于快速选择席位组合
- `seatIds`: 席位ID数组，最多5个

### 服务端消息 (ServerMessage)
服务端推送的事件格式：

```json
{
  "topicId": "string",
  "agentId": "string|long",    // "system" 或席位ID
  "eventType": "START|DELTA|SECTION|END|CANCELLED|ERROR|TOPIC_READY",
  "seq": "long",
  "content": "string",
  "payload": {
    "key": "value"
  }
}
```

- `topicId`: 讨论主题ID
- `agentId`: 智能体席位ID或"system"(系统事件)
- `eventType`: 事件类型
  - `TOPIC_READY`: 主题准备就绪（系统事件）
  - `START`: 智能体开始发言（payload包含席位元数据）
  - `DELTA`: 增量文本内容
  - `SECTION`: 段落分隔符
  - `END`: 正常结束
  - `CANCELLED`: 被取消
  - `ERROR`: 发生错误
- `seq`: 序列号，保证事件顺序（单调递增）
- `content`: 文本内容（DELTA事件）
- `payload`: 扩展数据（可选）

### START 事件 payload 结构
START 事件包含席位元数据，用于前端展示和初始化：

```json
{
  "codename": "string",         // 席位代号，用于显示
  "role": "string",             // 职业身份
  "avatarUrl": "string"         // 头像URL，可选
}
```

### SECTION 事件 payload 结构
SECTION 事件用于结构化内容展示，其 payload 必须遵循以下schema：

```json
{
  "sectionTitle": "string",     // 段落标题，必填
  "sectionType": "string",      // 段落类型：analysis|conclusion|recommendation|risks
  "content": "string",          // 段落完整内容，可选
  "bullets": ["string"],        //  bullet points，可选
  "risks": ["string"],          // 风险点列表，可选
  "actions": ["string"],        // 行动建议列表，可选
  "quoteRefs": ["string"]       // 引用来源，可选
}
```

### ERROR 事件 payload 结构
ERROR 事件必须包含错误信息和重试语义：

```json
{
  "errorCode": "string",        // 错误码枚举
  "retryable": "boolean",       // 是否可重试
  "message": "string",          // 用户可见错误描述
  "details": "object"           // 额外调试信息，可选
}
```

**标准错误码**:
- `INVALID_REQUEST`: 请求参数无效
- `SEAT_RESOLUTION_FAILED`: 席位解析失败
- `LLM_TIMEOUT`: LLM响应超时
- `LLM_RATE_LIMITED`: LLM速率限制
- `DELIVERY_FAILED`: 消息传递失败
- `TOPIC_NOT_FOUND`: 主题不存在
- `CONCURRENCY_LIMIT_EXCEEDED`: 并发限制超限

### 协议验收条款

#### 序列号单调递增
**验收标准**: 对同一 `topicId + agentId`，`seq` 必须严格递增，从 1 开始。
- ✅ 正确示例: agentId=1 的序列: 1(START) → 2(DELTA) → 3(SECTION) → 4(END)
- ❌ 错误示例: agentId=1 的序列: 1(START) → 3(DELTA) → 2(SECTION)

#### 终态事件唯一性
**验收标准**: 对同一 `topicId + agentId`，终态事件仅允许出现一次：`END` 或 `CANCELLED` 或 `ERROR`。
- ✅ 正确: 发送 END 后不再发送该 agent 的任何事件
- ❌ 错误: 发送 END 后又发送 DELTA 或另一个 END

#### 频道隔离
**验收标准**: 专属频道 `/topic/roundtable/{topicId}` 只承载该 `topicId` 的事件。
- ✅ 正确: topicId="abc" 的频道只收到 agentId 相关事件
- ❌ 错误: topicId="abc" 的频道收到 topicId="xyz" 的事件

#### 自动资源清理
**验收标准**: 终态事件发送后必须在 30 秒内清理所有相关资源。
- 清理内容: cancellation token、推送缓冲、topic级缓存
- 验收方式: 内存监控和定时检查

### 系统事件与席位事件分流
- **系统事件**: 使用 `agentId = "system"`，发布到控制频道 `/topic/roundtable/control`
- **席位事件**: 使用具体 `agentId`，发布到专属频道 `/topic/roundtable/{topicId}`
- **终态判定**: 以 `eventType` 为准，`isFinish` 仅用于兼容性

### 协议示例用例

#### 正常流程示例
```javascript
// 1. 创建主题
POST /api/topics
{ "topic": "如何提高团队协作效率" }

// 响应: { "topicId": "topic-123", ... }

// 2. 订阅专属频道
STOMP.subscribe('/topic/roundtable/topic-123')

// 3. 启动讨论
STOMP.send('/app/startDiscussion', {
  "action": "START",
  "topicId": "topic-123",
  "topic": "如何提高团队协作效率",
  "seatIds": [1, 2, 3, 4, 5]
})

// 4. 接收事件流
// 事件1:
{
  "topicId": "topic-123",
  "agentId": 1,
  "eventType": "START",
  "seq": 1,
  "content": "",
  "payload": {}
}

// 事件2:
{
  "topicId": "topic-123",
  "agentId": 1,
  "eventType": "DELTA",
  "seq": 2,
  "content": "团队协作",
  "payload": {}
}

// 事件3:
{
  "topicId": "topic-123",
  "agentId": 1,
  "eventType": "SECTION",
  "seq": 3,
  "content": "",
  "payload": {
    "sectionTitle": "核心问题分析",
    "sectionType": "analysis",
    "bullets": ["沟通障碍", "目标不一致", "工具使用不当"]
  }
}

// 事件4:
{
  "topicId": "topic-123",
  "agentId": 1,
  "eventType": "END",
  "seq": 4,
  "content": "",
  "payload": {}
}
```

#### 取消流程示例
```javascript
// 启动后发送取消
STOMP.send('/app/cancelDiscussion', {
  "action": "CANCEL",
  "topicId": "topic-123"
})

// 接收取消事件
{
  "topicId": "topic-123",
  "agentId": 1,
  "eventType": "CANCELLED",
  "seq": 2,
  "content": "",
  "payload": {}
}
```

#### 错误流程示例
```javascript
// 启动后遇到错误
{
  "topicId": "topic-123",
  "agentId": 2,
  "eventType": "ERROR",
  "seq": 1,
  "content": "",
  "payload": {
    "errorCode": "LLM_TIMEOUT",
    "retryable": true,
    "message": "智能体响应超时，请稍后重试"
  }
}
```
## 扩展

系统设计支持扩展：
- 集成真实LLM API（如OpenAI、Claude）
- 添加更多席位类型和模板
- 实现讨论历史记录
- 添加用户认证和权限控制</content>
<parameter name="filePath">/Users/alsay_mac/Synchronization/Github_File/MZJH/JAVA/README.md