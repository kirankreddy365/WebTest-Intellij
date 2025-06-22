#!/bin/bash

echo "Starting Selenium Grid via Docker Compose..."
docker-compose -f src/test/resources/docker/docker-compose.yml up -d

# Wait for Selenium Grid to be fully ready
sleep 10

echo "Running tests in parallel..."

mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=chrome  -Duse.grid=true &
mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=firefox -Duse.grid=true &
mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=edge    -Duse.grid=true &

wait  # Wait for all background tests to finish

echo "Stopping Selenium Grid..."
docker-compose -f src/test/resources/docker/docker-compose.yml down
