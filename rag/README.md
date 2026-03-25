# RAG Module

检索增强生成模块

## 功能

- 智能文档检索
- 知识库管理
- 语义搜索
- 问答生成

## 论文数据说明

- `rag/papers/papers_info.json` 会保留在仓库中，作为论文元数据与下载来源记录。
- `rag/papers/*.pdf` 默认不纳入 Git，以避免仓库体积持续膨胀。
- 如果需要重新准备知识库原文，可以先运行 `dataRetrieval/paper_crawler.py` 下载 PDF，再运行 `dataRetrieval/add_db.py` 重建向量库。

## 相关分支

- [master](../tree/master)
- [branch-model](../tree/branch-model)
- [branch-server](../tree/branch-server)
- [branch-web](../tree/branch-web)
