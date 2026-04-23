# AGENTS.md - 高光谱图像智能检测系统 开发指南

## 项目概述

高光谱图像智能检测系统是一个基于微服务架构的完整高光谱图像处理平台，集成了深度学习检测模型和RAG知识问答系统。

| **组件**         | **技术栈**                                        | **适用范围/版本**      |
| ---------------- | ------------------------------------------------- | ---------------------- |
| 后端服务          | Java 17 + Spring Boot 4.0.2 + MyBatis-Plus       | 全局                   |
| Web前端          | Vue 3 + Element Plus + Vite                      | 用户交互界面          |
| 模型服务          | Python 3.10 + Flask + PyTorch                     | 高光谱检测推理        |
| RAG知识问答      | Python 3.10 + Flask + LangChain + Chroma         | 知识库问答            |
| 数据库            | MySQL 8.4                                        | 数据持久化            |
| 缓存/会话         | Redis 7-alpine                                   | 会话管理              |

## 构建/测试命令

### 整体启动（Docker Compose）

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 后端服务（server）

```bash
cd server

# Maven构建
./mvnw clean package -DskipTests

# 本地运行（需要先启动MySQL和Redis）
./mvnw spring-boot:run
```

### 前端服务（web）

```bash
cd web

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产构建
npm run build
```

### 模型服务（model）

```bash
cd model

# 安装依赖
pip install -r requirements.txt

# 启动服务
python app.py
```

### RAG知识问答服务（rag）

```bash
cd rag

# 安装依赖
pip install -r requirements.txt

# 启动服务
python app.py
```

## 代码风格

### 命名约定

| **类型**                   | **规则**                                                 |
| -------------------------- | -------------------------------------------------------- |
| 组件/类文件                | PascalCase（`User.java`、`Detection.vue`）               |
| 工具函数/变量              | camelCase（`getUserInfo`、`taskList`）                  |
| 类型/接口                  | PascalCase（`UserInfo`）                                |
| 常量                       | UPPER_SNAKE_CASE（`MAX_FILE_SIZE`）                     |
| Python模块                 | snake_case（`data_loader.py`）                          |
| 数据库字段                 | snake_case（`user_id`、`create_time`）                  |
| 布尔变量                   | 必须包含 `is`、`has`、`can` 等前缀                      |
| 通用                       | 禁止中文命名、禁用模糊缩写（API/URL/ID/DB 等通用词除外） |

### 核心规范

- **Emoji 限制**：代码和注释中禁止使用 emoji，UI中可以使用 Element Plus 图标库。
- **语言表达**：所有专业术语、变量名、配置项在文档或注释中提及需附带中文说明。
- **作用域与导入**：在函数/代码块内使用模型或工具类时，必须确保该作用域内或文件头部有对应的完整 import。

### 数据库与数据规则

- **迁移管理**：数据库结构修改必须通过 **Flyway** 进行迁移管理，迁移文件位于 `server/src/main/resources/db/migration/`，命名格式为 `V{版本号}__{描述}.sql`。
- **数据访问**：使用 MyBatis-Plus 进行数据库操作，实体类使用 `@TableName` 注解。

### 部署与容器化

- **镜像架构**：所有 Docker 构建操作使用多平台架构支持，默认适用于 `linux/amd64` 和 `linux/arm64`。
- **数据持久化**：数据库、上传文件等重要数据通过 Docker volumes 持久化存储。

## 错误处理原则

### 必须遵循

- **先查证再开口**：不确定的事情先查证，查不到就说明查不到。
- **明确依据来源**：回答时说明依据（定位到哪个文件、哪行代码、哪个日志）。
- **不知道就是不知道**：如果无法解决，列出做了哪些尝试，最终为什么仍不确定。
- **出错就认**：排查方向或回答错误直接承认并纠正。

### 严禁

- 禁止猜测性断言（"应该是这样"、"可能没问题"）
- 禁止想当然（"一般项目都这样"）
- 禁止半吊子回答（查一半就急着下结论）
- 禁止信息编造与幻觉

## Docker 排查指南

**必须先通过 `docker`、`docker-compose` 命令实际查看状态，再作判断。**

排查流程：

1. 检查服务状态：`docker-compose ps`
2. 检查服务日志：`docker-compose logs <服务名>`
3. 检查容器资源使用：`docker stats`
4. 进入容器排查：`docker exec -it <容器名> /bin/bash`

常见服务名：web、server、model、rag、mysql、redis

## 问题处理流程

发现问题后**不要立即动手修**，先报告给用户，等用户确认方案后再改。

流程：

1. 明确描述问题、影响范围、推测的根因。
2. 提出建议修复方案（可提供多个选项），说明优缺点。
3. 等待用户确认。
4. 确认后执行代码级修复。

*例外情况（可直接修复）：明显拼写错误、导入缺失、用户明确指令"直接修"。*

## Git 规范

### 分支命名

格式：`<type>/<kebab-case-description>`

- 前缀：`feat`、`fix`、`refactor`、`chore`、`docs`、`perf`、`test`、`build`
- description 使用 kebab-case，2-5 个词，精准描述分支目标。
- 严禁：无意义名称（`temp`）、纯日期名称、中文/大写/下划线命名。

### Commit Message 格式

```
<type>(<scope>): <subject>
```

- type：与分支前缀一致。
- subject：**必须使用中文**，祈使语态，50字符内精准概括。
- 示例：`feat(detection): 新增 DSST 模型推理接口`

### 提交与合并要求

- **单元性提交**：每完成一个可独立描述、验证的最小完整改动，必须立即 commit。
- **禁止提交内容**：严禁提交 `.env`、敏感密钥、编译产物（如 `node_modules/`、`.venv/`、`dist/` 等）。

## 用户偏好

- 使用中文交流。
- 代码不加注释（除非特别要求或逻辑极度复杂）。
- 回答风格：简洁直接，拒绝废话。
- 优先考虑毕业设计论文的完整性和专业性。

## 破坏性操作确认

以下操作执行前，必须**逐项列出影响**并获得用户**明确授权确认**：

- 数据库高危操作（`DROP`、`DELETE`、`TRUNCATE`、数据表结构变更）
- Docker volumes 清理或数据删除
- 强制覆盖历史记录（`git push --force`、`git reset --hard`）

## 同源逻辑同步

修改一处逻辑后，必须全局搜索项目中是否存在相同或相似的逻辑副本，并全部同步修改。常见同步点：

| **逻辑类型** | **需同步检查的位置**                                   |
| ------------ | ------------------------------------------------------ |
| 数据校验规则 | 前端表单校验规则 & 后端 Java 校验                      |
| 接口定义     | 后端的 API Controller & 前端的 api.js / 请求封装       |
| 业务状态枚举 | 数据库 Model 定义 & 前端常量定义 & 后端实体类          |
| 文件上传配置 | 前端上传限制 & 后端 `application.yml` 配置             |

## 微服务架构说明

### 服务架构

本项目采用 Docker Compose 微服务架构，各服务职责：

- `web`：Vue 3 前端，提供用户交互界面
- `server`：Spring Boot 后端，业务逻辑、数据管理、API网关
- `model`：Flask 模型服务，高光谱图像检测推理（DSST/DMSSN）
- `rag`：Flask RAG服务，基于 LangChain + Chroma 的知识问答
- `mysql`：MySQL 数据库，数据持久化
- `redis`：Redis 缓存，会话管理

### 目录结构

```
.
├── web/           # 前端项目
│   ├── src/
│   │   ├── views/      # 页面组件
│   │   ├── api/        # API 请求
│   │   └── components/ # 通用组件
│   └── package.json
├── server/        # 后端项目
│   ├── src/main/java/org/example/server/
│   │   ├── controller/  # 控制器
│   │   ├── service/     # 服务层
│   │   ├── mapper/      # 数据访问
│   │   └── domain/      # 实体类
│   └── src/main/resources/
│       ├── db/migration/ # Flyway 迁移文件
│       └── application.yml
├── model/         # 模型服务
│   ├── models/   # 模型定义
│   ├── modules/  # 模块组件
│   └── assistant/ # 辅助工具
├── rag/          # RAG知识问答
│   ├── chroma_db/ # Chroma向量库
│   ├── docs/      # 知识库文档
│   └── papers/    # 论文资料
├── docker/       # Docker配置
│   └── mysql/init/ # 数据库初始化
└── docker-compose.yml
```

### 数据流与通信

- 前端通过 HTTP API 与后端服务通信
- 后端通过 HTTP 调用模型服务和 RAG 服务
- 所有服务通过 Docker 网络互联，服务名即 DNS 名称
- 文件通过 Docker volumes 在服务间共享
