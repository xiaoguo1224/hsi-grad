FROM python:3.10-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# 安装系统依赖（不常变化的层）
RUN apt-get update --allow-releaseinfo-change \
    && apt-get install -y --no-install-recommends build-essential libglib2.0-0 libsm6 libxext6 libxrender1 \
    && rm -rf /var/lib/apt/lists/*

# 先只复制 requirements.txt（这层会被缓存）
COPY requirements.txt ./

# 配置pip源并安装依赖（这层只要requirements.txt不变就不会重新构建）
RUN pip config set global.index-url https://pypi.tuna.tsinghua.edu.cn/simple \
    && pip config set install.trusted-host pypi.tuna.tsinghua.edu.cn \
    && pip install --no-cache-dir -r requirements.txt

# 最后复制应用代码（代码变化时只重新构建这层）
COPY . .

EXPOSE 5000

CMD ["gunicorn", "-w", "1", "-b", "0.0.0.0:5000", "app:app", "--timeout", "300"]
