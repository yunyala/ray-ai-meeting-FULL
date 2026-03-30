#!/bin/bash

echo "正在启动 Ray AI Meeting Backend..."
echo "请确保已设置 DASHSCOPE_API_KEY 环境变量"

# 强制更新依赖
mvn clean install -U

# 启动应用
mvn spring-boot:run
