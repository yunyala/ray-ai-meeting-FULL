# 快速开始指南

## 第一步：获取阿里云配置

### 1. DashScope API Key（用于 LLM 总结）

1. 访问：https://dashscope.aliyun.com/
2. 登录/注册阿里云账号
3. 点击"开通 DashScope"
4. 进入控制台，创建 API Key
5. 复制 API Key 备用

### 2. 语音识别配置（用于语音转文字）

1. 访问：https://nls-portal.console.aliyun.com/
2. 开通"录音文件识别极速版"服务
3. 创建项目，获取 AppKey
4. 在 RAM 访问控制中创建 AccessKey：
   - 访问：https://ram.console.aliyun.com/
   - 创建用户并授予"智能语音交互"权限
   - 获取 AccessKeyId 和 AccessKeySecret

## 第二步：配置环境变量

### Windows 用户

创建 `start-all.bat` 文件：

```batch
@echo off

REM 设置环境变量
set DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxx
set ALIYUN_ASR_APP_KEY=xxxxxxxxxxxxx
set ALIYUN_ACCESS_KEY_ID=xxxxxxxxxxxxx
set ALIYUN_ACCESS_KEY_SECRET=xxxxxxxxxxxxx

REM 启动后端服务
start cmd /k "cd ray-ai-meeting-backend && mvn spring-boot:run"

REM 等待 10 秒
timeout /t 10

REM 启动前端服务
start cmd /k "cd ray-ai-meeting-frontend && npm run dev"

echo 所有服务已启动！
echo 前端地址: http://localhost:3000
echo 后端地址: http://localhost:8080
pause
```

### Linux/Mac 用户

创建 `start-all.sh` 文件：

```bash
#!/bin/bash

# 设置环境变量
export DASHSCOPE_API_KEY=sk-xxxxxxxxxxxxx
export ALIYUN_ASR_APP_KEY=xxxxxxxxxxxxx
export ALIYUN_ACCESS_KEY_ID=xxxxxxxxxxxxx
export ALIYUN_ACCESS_KEY_SECRET=xxxxxxxxxxxxx

# 启动后端服务
cd ray-ai-meeting-backend
mvn spring-boot:run &
BACKEND_PID=$!

# 等待 10 秒
sleep 10

# 启动前端服务
cd ../ray-ai-meeting-frontend
npm run dev &
FRONTEND_PID=$!

echo "所有服务已启动！"
echo "前端地址: http://localhost:3000"
echo "后端地址: http://localhost:8080"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
wait
```

然后执行：
```bash
chmod +x start-all.sh
./start-all.sh
```

## 第三步：安装依赖

### 后端依赖

```bash
cd ray-ai-meeting-backend
mvn clean install -U
```

### 前端依赖

```bash
cd ray-ai-meeting-frontend
npm install
```

## 第四步：启动服务

### 方式一：使用启动脚本（推荐）

Windows: 双击 `start-all.bat`
Linux/Mac: 执行 `./start-all.sh`

### 方式二：手动启动

1. 终端 1 - 启动后端：
```bash
cd ray-ai-meeting-backend
export DASHSCOPE_API_KEY=your-key
export ALIYUN_ASR_APP_KEY=your-key
export ALIYUN_ACCESS_KEY_ID=your-key
export ALIYUN_ACCESS_KEY_SECRET=your-key
mvn spring-boot:run
```

2. 终端 2 - 启动前端：
```bash
cd ray-ai-meeting-frontend
npm run dev
```

### 方式三：Docker Compose

```bash
docker-compose up -d
```

## 第五步：访问应用

打开浏览器访问：http://localhost:3000

## 使用说明

1. 选择会议场景（财务对账会 / 人事招聘会）
2. 点击"开始录音"按钮
3. 允许浏览器访问麦克风
4. 开始说话，系统会实时显示转录结果
5. 系统会自动识别不同说话人
6. 每 10 条转录后自动生成会议总结
7. 点击"停止录音"结束会议

## 常见问题

### Q: 麦克风无法访问
A: 检查浏览器权限设置，确保允许网站访问麦克风

### Q: 识别结果为空
A: 检查阿里云 ASR 配置是否正确，账户余额是否充足

### Q: 后端启动失败
A: 执行 `mvn clean install -U` 强制更新依赖

### Q: 前端无法连接后端
A: 确保后端服务已启动在 8080 端口

## 费用说明

### 阿里云 DashScope
- 免费额度：每月 100 万 tokens
- 超出后按量计费

### 阿里云语音识别
- 录音文件识别极速版：按时长计费
- 新用户有免费试用额度

## 技术支持

如遇问题，请查看：
- README.md - 项目说明
- SETUP.md - 详细配置指南
- 项目 Issues
