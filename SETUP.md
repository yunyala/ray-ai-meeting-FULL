# 项目配置指南

## 依赖问题修复

如果遇到 Spring AI Alibaba 依赖下载失败，请按以下步骤操作：

### 1. 清理本地 Maven 缓存

```bash
# Windows
rmdir /s /q %USERPROFILE%\.m2\repository\com\alibaba\cloud\ai

# Linux/Mac
rm -rf ~/.m2/repository/com/alibaba/cloud/ai
```

### 2. 强制更新依赖

```bash
cd ray-ai-meeting-backend
mvn clean install -U
```

### 3. 验证依赖配置

确保 `pom.xml` 包含以下配置：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.alibaba.cloud.ai</groupId>
            <artifactId>spring-ai-alibaba-bom</artifactId>
            <version>1.1.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
    </dependency>
</dependencies>
```

## 环境变量配置

### DashScope API Key（LLM 服务）

1. 访问 [阿里云百炼平台](https://dashscope.aliyun.com/)
2. 注册并获取 API Key
3. 设置环境变量：

```bash
# Windows
set DASHSCOPE_API_KEY=your-api-key-here

# Linux/Mac
export DASHSCOPE_API_KEY=your-api-key-here
```

### 阿里云语音识别配置

1. 访问 [阿里云智能语音交互](https://nls-portal.console.aliyun.com/)
2. 开通录音文件识别服务
3. 获取配置信息：
   - AppKey：在控制台创建项目后获得
   - AccessKeyId 和 AccessKeySecret：在 RAM 访问控制中创建
4. 在后端配置文件或环境变量中设置：

```bash
ALIYUN_ASR_APP_KEY=your-app-key
ALIYUN_ACCESS_KEY_ID=your-access-key-id
ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret
```

## 快速启动

### 方式一：使用启动脚本

```bash
# Windows
cd ray-ai-meeting-backend
start.bat

# Linux/Mac
cd ray-ai-meeting-backend
chmod +x start.sh
./start.sh
```

### 方式二：Docker Compose

```bash
# 设置环境变量
export DASHSCOPE_API_KEY=your-api-key-here

# 启动所有服务
docker-compose up -d
```

### 方式三：手动启动

1. 启动后端服务：
```bash
cd ray-ai-meeting-backend
export DASHSCOPE_API_KEY=your-api-key-here
export ALIYUN_ASR_APP_KEY=your-app-key
export ALIYUN_ACCESS_KEY_ID=your-access-key-id
export ALIYUN_ACCESS_KEY_SECRET=your-access-key-secret
mvn spring-boot:run
```

2. 启动前端应用：
```bash
cd ray-ai-meeting-frontend
npm install
npm run dev
```

## 常见问题

### Q: Maven 依赖下载失败
A: 执行 `mvn clean install -U` 强制更新依赖

### Q: 无法连接到语音识别服务
A: 检查阿里云 ASR 配置是否正确，网络是否正常

### Q: WebSocket 连接失败
A: 检查后端服务是否正常运行在 8080 端口

### Q: 前端无法访问后端 API
A: 检查 CORS 配置和代理设置

## 参考文档

- [Spring AI Alibaba 官方文档](https://java2ai.com/docs)
- [Spring AI 文档](https://docs.spring.io/spring-ai/reference/)
- [阿里云智能语音交互](https://help.aliyun.com/product/30413.html)
- [阿里云百炼平台](https://help.aliyun.com/zh/dashscope/)
