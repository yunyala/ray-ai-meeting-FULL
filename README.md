# Ray AI Meeting Assistant

实时会议助手系统，支持语音转文字、说话人识别和基于 LLM 的场景化总结。

## 项目结构

```
ray-ai-meeting-FULL/
├── ray-ai-meeting-frontend/    # Vue 3 + TypeScript + Vite 前端
└── ray-ai-meeting-backend/     # Spring Boot 3.4 + Spring AI Alibaba 后端
```

## 技术栈

### 前端 (Frontend)
- Vue 3 + TypeScript + Vite
- Element Plus UI 组件库
- WebSocket 实时通信
- 配色方案：深灰 + 琥珀金/翡翠绿

### 后端 (Backend)
- Java 21
- Spring Boot 3.4
- Spring AI Alibaba 1.1.2.0
- WebSocket 支持
- 阿里云语音识别服务集成
- 遵循阿里巴巴 Java 开发规范

## 核心功能

1. **实时语音转文字**：调用阿里云语音识别服务进行高精度转录
2. **说话人识别**：自动识别不同说话人
3. **场景化总结**：支持财务对账会和人事招聘会两种场景
4. **实时推送**：WebSocket 实时推送转录结果
5. **智能提取**：自动提取待办事项、决策点和风险项

## 环境要求

- Node.js 18+
- Java 21
- Maven 3.8+
- 阿里云账号（用于语音识别和 LLM 服务）

## 配置说明

### 1. 阿里云 DashScope API Key

用于 LLM 总结功能：

1. 访问 [阿里云百炼平台](https://dashscope.aliyun.com/)
2. 注册并获取 API Key
3. 设置环境变量：

```bash
export DASHSCOPE_API_KEY=your-dashscope-api-key
```

### 2. 阿里云语音识别配置

用于语音转文字和说话人分离：

1. 访问 [阿里云智能语音交互](https://nls-portal.console.aliyun.com/)
2. 开通录音文件识别服务
3. 获取 AppKey、AccessKeyId 和 AccessKeySecret
4. 设置环境变量：

```bash
export ALIYUN_ASR_APP_KEY=your-app-key
export ALIYUN_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret
```

## 快速开始

### 1. 启动后端服务

```bash
cd ray-ai-meeting-backend

# 设置环境变量
export DASHSCOPE_API_KEY=your-dashscope-api-key
export ALIYUN_ASR_APP_KEY=your-app-key
export ALIYUN_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret

# 使用 Maven 启动
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 2. 启动前端应用

```bash
cd ray-ai-meeting-frontend
npm install
npm run dev
```

前端应用将在 `http://localhost:3000` 启动。

## 会议场景

### 财务对账会
- 提取财务任务、对账项、审批事项
- 识别财务决策、预算批准、支付安排
- 标记财务风险、合规问题、资金缺口

### 人事招聘会
- 记录面试安排、背景调查、offer 准备
- 总结候选人评价、面试结果、薪资范围
- 识别候选人风险、匹配度问题、竞争风险

## API 文档

### WebSocket 端点

- **前端 → 后端**：`ws://localhost:8080/ws/meeting?scene={finance|hr}`

### 消息格式

```json
{
  "type": "transcript",
  "data": {
    "speaker": "Speaker_A",
    "text": "会议内容...",
    "start": 0.0,
    "end": 1.5
  }
}
```

## 开发规范

- Java 代码遵循阿里巴巴 Java 开发规范
- 所有公共方法必须包含 Javadoc 注释
- 前端使用 Composition API (`<script setup>`)

## 许可证

MIT License

## 作者

Ray - Senior Full-Stack Engineer & AI Architect
