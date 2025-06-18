@echo off
echo Starting Selenium Grid...
docker-compose -f src\test\resources\docker\docker-compose.yml up -d selenium-hub chrome firefox edge

echo Waiting for Selenium Grid to be ready...
timeout /t 10 /nobreak

echo Running tests in Docker...
docker-compose -f src\test\resources\docker\docker-compose.test.yml up --build test-app

echo Tests completed. Stopping containers...
docker-compose -f src\test\resources\docker\docker-compose.yml down

echo Test results are available in the test-output directory
pause 