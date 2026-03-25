FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG VITE_APP_BASE_API=/api
ARG VITE_APP_API_URL=http://server:8080
ARG VITE_RAG_API=/api/rag

ENV VITE_APP_BASE_API=${VITE_APP_BASE_API}
ENV VITE_APP_API_URL=${VITE_APP_API_URL}
ENV VITE_RAG_API=${VITE_RAG_API}

RUN npm run build

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
