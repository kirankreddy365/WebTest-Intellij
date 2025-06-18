@echo off
echo Testing Docker setup...

echo Checking if Docker is running...
docker --version
if %errorlevel% neq 0 (
    echo Docker is not installed or not running!
    pause
    exit /b 1
)

echo Creating test-output directory...
if not exist "test-output" mkdir test-output
if not exist "test-output\screenshots" mkdir test-output\screenshots
if not exist "test-output\extent-reports" mkdir test-output\extent-reports

echo Starting Selenium Grid...
docker-compose -f src\test\resources\docker\docker-compose.yml up -d selenium-hub chrome

echo Waiting for Selenium Grid to be ready...
timeout /t 15 /nobreak

echo Checking if containers are running...
docker-compose -f src\test\resources\docker\docker-compose.yml ps

echo Checking Selenium Grid status...
curl -s http://localhost:4444/wd/hub/status

echo Test setup completed. You can now run the full test suite.
pause 