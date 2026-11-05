@echo off
SETLOCAL
cd /d %~dp0
echo [1/3] Building frontend...
cd frontend
call npm install
if errorlevel 1 exit /b 1
call npm run build
if errorlevel 1 exit /b 1
cd ..
echo [2/3] Copying frontend dist into backend static...
if not exist backend\src\main\resources\static mkdir backend\src\main\resources\static
xcopy /E /Y /I frontend\dist\* backend\src\main\resources\static\ >nul
echo [3/3] Packaging backend jar...
cd backend
call mvnw.cmd clean package -DskipTests
if errorlevel 1 exit /b 1
echo Done. Run: java -jar target\home-connect-service-1.0.0.jar
ENDLOCAL
