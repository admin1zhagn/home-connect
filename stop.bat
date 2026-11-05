@echo off
REM ============================================================
REM  Family SmartLink Service V1.0 - stop all (Windows)
REM  Kills processes listening on ports 8080 and 5173.
REM ============================================================
echo ==================================================
echo  Family SmartLink Service V1.0 - Stop
echo ==================================================

set "FOUND=0"

REM ---- Stop backend (8080) ----
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Stopping backend process PID %%a ...
    taskkill /F /PID %%a >nul 2>&1
    set "FOUND=1"
)

REM ---- Stop frontend (5173) ----
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5173 " ^| findstr "LISTENING"') do (
    echo Stopping frontend process PID %%a ...
    taskkill /F /PID %%a >nul 2>&1
    set "FOUND=1"
)

if "%FOUND%"=="0" (
    echo No running service found on 8080 / 5173.
) else (
    echo Stop signal sent.
)
echo.
pause
