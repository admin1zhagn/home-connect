#!/usr/bin/env bash
# ============================================================
#  家庭智联服务系统 V1.0 - 一键停止 (Git Bash / Linux / macOS)
#  按端口定位并关闭后端(8080)与前端(5173)进程
# ============================================================
echo "=================================================="
echo " 家庭智联服务系统 V1.0 - 一键停止"
echo "=================================================="

stop_port() {
  local port="$1"
  local pid
  pid=$(lsof -ti tcp:"$port" 2>/dev/null || true)
  if [ -n "$pid" ]; then
    echo "停止端口 $port 的进程 PID: $pid"
    kill -9 $pid 2>/dev/null || true
  else
    echo "端口 $port 未发现运行中的服务。"
  fi
}

stop_port 8080
stop_port 5173
echo "完成。"
