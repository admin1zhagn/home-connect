@echo off
REM ============================================================
REM  Family SmartLink Service V1.0 - backend only (Windows)
REM  Backend : http://localhost:8080   (Spring Boot + H2)
REM  Account : admin / admin123
REM  Requires: JDK 17+ on PATH
REM  For frontend too, use start.bat instead.
REM ============================================================
setlocal
set "ROOT=%~dp0"
set "BACKEND=%ROOT%backend"

echo ==================================================
echo  Family SmartLink Service V1.0 - Backend (Windows)
echo  Backend : http://localhost:8080
echo  Account : admin / admin123
echo ==================================================

cd /d "%BACKEND%"
if not exist "target\home-connect-service-1.0.0.jar" (
    echo [Backend] jar not found, building with Maven...
    call mvnw.cmd package -DskipTests
)
echo [Backend] starting on port 8080 (close this window to stop)...
java -jar target\home-connect-service-1.0.0.jar --server.port=8080
pause
