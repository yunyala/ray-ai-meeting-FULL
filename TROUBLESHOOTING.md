# 故障排查指南

## WebSocket 连接问题

### 症状：WebSocket 连接后立即关闭

#### 检查步骤：

1. **确认后端服务正常运行**
   ```bash
   curl http://localhost:8080/api/health
   ```
   应该返回：
   ```json
   {
     "status": "UP",
     "service": "Ray AI Meeting Backend",
     "timestamp": "...",
     "version": "1.0.0"
   }
   ```

2. **检查后端日志**
   查看后端控制台，应该看到：
   ```
   WebSocket 连接建立: xxx, URI: ws://localhost:8080/ws/meeting?scene=finance
   会议场景: finance, 连接成功
   ```
   
   如果看到错误，记录错误信息。

3. **检查前端浏览器控制台**
   打开浏览器开发者工具（F12），查看 Console 标签：
   - 应该看到 "正在连接 WebSocket: ..."
   - 应该看到 "WebSocket 连接成功"
   
   如果看到 "WebSocket 连接关闭"，记录关闭代码和原因。

4. **检查网络请求**
   在浏览器开发者工具的 Network 标签中：
   - 筛选 WS（WebSocket）
   - 查看 ws://localhost:8080/ws/meeting 的状态
   - 点击查看详细信息，包括握手请求和响应

### 常见问题和解决方案

#### 问题 1：端口冲突
**症状**：无法连接到 8080 端口

**解决方案**：
```bash
# Windows 查看端口占用
netstat -ano | findstr :8080

# Linux/Mac 查看端口占用
lsof -i :8080

# 修改后端端口（application.yml）
server:
  port: 8081
```

#### 问题 2：CORS 跨域错误
**症状**：浏览器控制台显示 CORS 错误

**解决方案**：
- 确认 `CorsConfig.java` 已正确配置
- 重启后端服务
- 清除浏览器缓存

#### 问题 3：WebSocket 握手失败
**症状**：状态码 400 或 403

**解决方案**：
- 检查 URL 格式是否正确
- 确认 WebSocketConfig 配置正确
- 查看后端日志中的详细错误

#### 问题 4：依赖注入失败
**症状**：后端启动时报错，提示找不到 Bean

**解决方案**：
```bash
cd ray-ai-meeting-backend
mvn clean install -U
mvn spring-boot:run
```

#### 问题 5：前端无法获取麦克风权限
**症状**：点击"开始录音"没有反应

**解决方案**：
- 使用 HTTPS 或 localhost（HTTP 也可以）
- 检查浏览器麦克风权限设置
- 尝试其他浏览器（推荐 Chrome/Edge）

### 调试技巧

#### 1. 启用详细日志

在 `application.yml` 中：
```yaml
logging:
  level:
    com.ray.ai.meeting: DEBUG
    org.springframework.web.socket: DEBUG
```

#### 2. 使用 WebSocket 测试工具

推荐工具：
- [WebSocket King Client](https://websocketking.com/)
- Postman（支持 WebSocket）

测试 URL：
```
ws://localhost:8080/ws/meeting?scene=finance
```

#### 3. 检查防火墙设置

确保防火墙允许 8080 端口的入站连接。

#### 4. 测试音频录制

在浏览器控制台运行：
```javascript
navigator.mediaDevices.getUserMedia({ audio: true })
  .then(stream => {
    console.log('麦克风访问成功', stream)
    stream.getTracks().forEach(track => track.stop())
  })
  .catch(err => console.error('麦克风访问失败', err))
```

### 环境变量检查

确认以下环境变量已正确设置：

```bash
# 必需
DASHSCOPE_API_KEY=sk-xxxxx

# 可选（如果不设置，会使用模拟数据）
ALIYUN_ASR_APP_KEY=xxxxx
ALIYUN_ACCESS_KEY_ID=xxxxx
ALIYUN_ACCESS_KEY_SECRET=xxxxx
```

### 完整的启动流程

1. **启动后端**
   ```bash
   cd ray-ai-meeting-backend
   export DASHSCOPE_API_KEY=your-key
   export ALIYUN_ASR_APP_KEY=your-key
   export ALIYUN_ACCESS_KEY_ID=your-key
   export ALIYUN_ACCESS_KEY_SECRET=your-key
   mvn spring-boot:run
   ```

2. **验证后端**
   ```bash
   curl http://localhost:8080/api/health
   ```

3. **启动前端**
   ```bash
   cd ray-ai-meeting-frontend
   npm run dev
   ```

4. **访问应用**
   打开浏览器访问：http://localhost:3000

5. **测试 WebSocket**
   - 点击"开始录音"
   - 允许麦克风权限
   - 查看控制台日志

### 获取帮助

如果以上步骤都无法解决问题，请提供以下信息：

1. 操作系统和版本
2. Java 版本（`java -version`）
3. Node.js 版本（`node -v`）
4. 后端完整日志
5. 前端浏览器控制台日志
6. Network 标签中的 WebSocket 请求详情
