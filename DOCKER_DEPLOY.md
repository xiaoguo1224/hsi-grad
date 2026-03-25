# Docker 部署说明

这个仓库现在被整理为一套 `docker compose` 多容器部署方案，包含以下服务：

- `web`: Vue 生产构建后的 Nginx 静态站点
- `server`: Spring Boot 后端
- `model`: 高光谱识别 Flask/Gunicorn 服务
- `rag`: RAG Flask/Gunicorn 服务
- `mysql`: MySQL 8.4
- `redis`: Redis 7

说明：`Ollama` 默认复用宿主机本地服务，不在 Compose 内单独起容器。

## 代码结构分析结论

- `web` 通过 `VITE_APP_BASE_API` 调后端接口，通过 `VITE_RAG_API` 调 RAG 接口。
- `server` 依赖 MySQL，并通过 `predict.url` 调用 `model` 服务。
- `model` 会输出识别结果图片，必须和 `server` 共享上传目录。
- `rag` 依赖 Redis 和 Ollama，向量库目录为 `rag/chroma_db`。
- `rag` 通过 `OLLAMA_BASE_URL` 连接你本机的 Ollama，默认地址是 `http://host.docker.internal:11434`。
- `rag/papers/papers_info.json` 保留论文元数据；PDF 原文默认不提交到 Git，需要时可重新下载。

## 首次准备

1. 复制环境变量模板：

```bash
cp .env.docker.example .env
```

`.env` 里已经固定了：

```text
COMPOSE_PROJECT_NAME=hsi-stack
```

这是为了避免在中文目录下运行 `docker compose` 时出现 `project name must not be empty`。

2. 当前仓库已经接入根目录下的 MySQL dump 文件 `_localhost-2026_03_25_09_36_12-dump.sql`，首次启动时会自动导入。

3. 如果后续你想替换初始化数据，可以：

- 直接替换根目录这个 dump 文件
- 或者把新的 `.sql` 文件放到 `docker/mysql/init/` 目录

## 启动

```bash
docker compose up -d --build
```

启动前请先确认本机 Ollama 已经运行，并且模型已经存在：

```bash
ollama list
ollama pull qwen3:8b
ollama pull qwen3-embedding:0.6b
```

如果你需要重建 RAG 论文知识库，建议先下载论文原文，再执行向量库构建脚本：

```bash
python rag/dataRetrieval/paper_crawler.py
python rag/dataRetrieval/add_db.py
```

前端默认访问地址：

```text
http://localhost
```

如果你之前已经启动过旧的 MySQL 卷，想重新执行初始化 SQL，需要先删除旧数据卷再启动：

```bash
docker compose down -v
docker compose up -d --build
```

## 常用端口

- `web`: `80`
- `server`: `8080`
- `model`: `5000`
- `rag`: `5001`
- `mysql`: `3306`
- `redis`: `6379`

本机 `ollama` 默认端口：

- `ollama`: `11434`

## 注意事项

- `rag` 依赖你本机已启动的 Ollama；如果容器内连不上，可以先在宿主机访问 `http://localhost:11434/api/tags` 自查。
- `model` 镜像会安装 `torch`，构建时间会比较长。
- MySQL 只会在数据目录为空时执行初始化 SQL；如果你修改了 dump 但没清卷，容器不会自动重复导入。
