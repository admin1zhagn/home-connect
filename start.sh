#!/usr/bin/env bash
# ============================================================
#  Family SmartLink Service (家庭智联服务系统) V1.0
#  One-click startup for Git Bash / Linux / macOS
#  Backend : http://localhost:8080   (Spring Boot + H2)
#  Frontend: http://localhost:5173   (Vue3 + Vite)
#  Account : admin / admin123
#  Requires: JDK 17+ on PATH, Node.js 18+ on PATH
# ============================================================
set -e
ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND="$ROOT/backend"
FRONTEND="$ROOT/frontend"

echo "=================================================="
echo " 家庭智联服务系统 V1.0 - 一键启动 (Bash)"
echo " Backend : http://localhost:8080"
echo " Frontend: http://localhost:5173"
echo " Account : admin / admin123"
echo "=================================================="

# ---- Backend ----
cd "$BACKEND"
if [ ! -f "target/home-connect-service-1.0.0.jar" ]; then
  echo "[Backend] jar not found, building with Maven..."
  ./mvnw package -DskipTests
fi
echo "[Backend] launching on port 8080 ..."
java -jar target/home-connect-service-1.0.0.jar --server.port=8080 > "$ROOT/backend.log" 2>&1 &
BACK_PID=$!

# ---- Frontend ----
cd "$FRONTEND"
if [ ! -d "node_modules" ]; then
  echo "[Frontend] node_modules missing, running npm install..."
  npm install
fi
echo "[Frontend] launching Vite dev server on 5173 ..."
npm run dev > "$ROOT/frontend.log" 2>&1 &
FRONT_PID=$!

echo ""
echo "Backend  PID=$BACK_PID  (log: $ROOT/backend.log)"
echo "Frontend PID=$FRONT_PID  (log: $ROOT/frontend.log)"
echo "Open http://localhost:5173 and login with admin / admin123"
echo "Press Ctrl+C to stop both services."
wait
