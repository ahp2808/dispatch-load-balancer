@echo off
setlocal
set "DIR=%~dp0"
if exist "%DIR%.tools\apache-maven-3.9.9\bin\mvn.cmd" (
    call "%DIR%.tools\apache-maven-3.9.9\bin\mvn.cmd" %*
) else (
    where mvn >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        call mvn %*
    ) else (
        echo Error: Maven not found in .tools or PATH.
        exit /b 1
    )
)
