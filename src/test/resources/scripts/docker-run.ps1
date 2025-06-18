# PowerShell script to run tests in Docker

Write-Host "Creating test-output directory..." -ForegroundColor Green
if (!(Test-Path "test-output")) { New-Item -ItemType Directory -Path "test-output" }
if (!(Test-Path "test-output\screenshots")) { New-Item -ItemType Directory -Path "test-output\screenshots" }
if (!(Test-Path "test-output\extent-reports")) { New-Item -ItemType Directory -Path "test-output\extent-reports" }

Write-Host "Starting Selenium Grid..." -ForegroundColor Green
docker-compose -f src\test\resources\docker\docker-compose.yml up -d selenium-hub chrome firefox edge

Write-Host "Waiting for Selenium Grid to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "Running tests in Docker..." -ForegroundColor Green
docker-compose -f src\test\resources\docker\docker-compose.test.yml up --build test-app

Write-Host "Tests completed. Stopping containers..." -ForegroundColor Yellow
docker-compose -f src\test\resources\docker\docker-compose.yml down

Write-Host "Test results are available in the test-output directory" -ForegroundColor Green
Read-Host "Press Enter to continue" 