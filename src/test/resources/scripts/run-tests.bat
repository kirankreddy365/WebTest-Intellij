@echo off

REM Start Selenium Grid
 echo Starting Selenium Grid via Docker Compose...
docker-compose -f src/test/resources/docker/docker-compose.yml up -d

REM Wait for Selenium Grid to be fully ready
echo Waiting for Selenium Grid to be ready...
timeout /t 10 /nobreak >nul

REM Run tests in parallel
echo Running tests in parallel...
start /B cmd /c "mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=chrome  -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub && echo CHROME_DONE > chrome.status"
start /B cmd /c "mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=firefox -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub && echo FIREFOX_DONE > firefox.status"
start /B cmd /c "mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=edge    -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub && echo EDGE_DONE > edge.status"

echo Tests started in parallel. Waiting for all tests to complete...

:wait_loop
if exist chrome.status (
    if exist firefox.status (
        if exist edge.status (
            goto cleanup
        )
    )
)
echo Tests still running... waiting 30 seconds
timeout /t 30 /nobreak >nul
goto wait_loop

:cleanup
echo All tests completed!
del chrome.status firefox.status edge.status 2>nul

REM Stop Selenium Grid
echo Stopping Selenium Grid...
docker-compose -f src/test/resources/docker/docker-compose.yml down

echo Done! Check test-output directory for results.
pause 