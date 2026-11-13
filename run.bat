@echo off
chcp 65001 > nul
echo [1/2] Compiling source code...
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin @sources.txt
if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed! Please check your code.
    del sources.txt 2>nul
    pause
    exit /b %errorlevel%
)
del sources.txt 2>nul
echo [2/2] Launching application...
echo ===================================================
java -cp bin Main
pause
