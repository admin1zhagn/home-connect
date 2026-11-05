@echo off
REM ============================================================
REM  Family SmartLink Service V1.0 - one-click startup (Windows)
REM  Backend : http://localhost:8080   (Spring Boot + H2)
REM  Frontend: http://localhost:5173   (Vue3 + Vite)
REM  Account : admin / admin123
REM  Requires: JDK 17+ on PATH, Node.js 18+ on PATH
REM ============================================================
setlocal
set "ROOT=%~dp0"
set "BACKEND=%ROOT%backend"
set "FRONTEND=%ROOT%frontend"

echo ==================================================
echo  Family SmartLink Service V1.0 - Start (Windows)
echo  Backend : http://localhost:8080
echo  Frontend: http://localhost:5173
echo  Account : admin / admin123
echo ==================================================

REM ---- Backend ----
cd /d "%BACKEND%"
if not exist "target\home-connect-service-1.0.0.jar" (
    echo [Backend] jar not found, building with Maven...
    call mvnw.cmd package -DskipTests
)
echo [Backend] starting on port 8080 ...
start "HomeConnect-Backend" cmd /k "java -jar target\home-connect-service-1.0.0.jar --server.port=8080"

REM ---- Frontend ----
cd /d "%FRONTEND%"
if not exist "node_modules" (
    echo [Frontend] node_modules not found, running npm install...
    call npm install
)
echo [Frontend] starting dev server on port 5173 ...
start "HomeConnect-Frontend" cmd /k "npm run dev"

echo.
echo Both services have been started in separate windows.
echo Open http://localhost:5173 in your browser, login admin / admin123.
echo Close those two windows to stop the services.
echo.
pause
