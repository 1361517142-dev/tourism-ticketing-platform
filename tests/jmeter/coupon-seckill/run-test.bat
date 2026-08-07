@echo off
setlocal
chcp 65001 >nul

title Tourism Coupon Seckill Test

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0run-test.ps1"
set "TEST_EXIT_CODE=%ERRORLEVEL%"

echo.
if "%TEST_EXIT_CODE%"=="0" (
    echo Test completed. The report has been generated.
) else (
    echo Test failed. See the error message above.
)

echo.
pause
exit /b %TEST_EXIT_CODE%
