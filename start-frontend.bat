@echo off
REM ============================================================
REM  Family SmartLink Service V1.0 - frontend only (Windows)
REM  Frontend: http://localhost:5173   (Vue3 + Vite)
REM  Requires: Node.js 18+ on PATH
REM  Note: start backend first (start-backend.bat or start.bat)
REM ============================================================
setlocal
set "ROOT=%~dp0"
set "FRONTEND=%ROOT%frontend"

echo ==================================================
echo  Family SmartLink Service V1.0 - Frontend (Windows)
echo  Frontend: http://localhost:5173
echo ==================================================

cd /d "%FRONTEND%"
if not exist "node_modules" (
    echo [Frontend] node_modules not found, running npm install...
    call npm install
)
echo [Frontend] starting dev server on port 5173 (close this window to stop)...
npm run dev
pause
