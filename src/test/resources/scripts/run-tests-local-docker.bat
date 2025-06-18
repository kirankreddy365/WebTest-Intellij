@echo off
echo Creating test-output directory...
if not exist "test-output" mkdir test-output
if not exist "test-output\screenshots" mkdir test-output\screenshots
if not exist "test-output\extent-reports" mkdir test-output\extent-reports

echo Starting Selenium Grid in Docker...
docker-compose -f src\test\resources\docker\docker-compose.yml up -d selenium-hub chrome firefox edge

echo Waiting for Selenium Grid to be ready...
timeout /t 15 /nobreak

echo Running tests locally against Docker Grid...
mvn clean test -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub -Dbrowser=chrome -DisHeadless=true

echo Tests completed. Stopping Docker containers...
docker-compose -f src\test\resources\docker\docker-compose.yml down

echo Test results are available in the test-output directory
pause 