@echo off
echo 正在启动 Ray AI Meeting Backend...
echo 请确保已设置 DASHSCOPE_API_KEY 环境变量

REM 强制更新依赖
call mvn clean install -U

REM 启动应用
call mvn spring-boot:run
