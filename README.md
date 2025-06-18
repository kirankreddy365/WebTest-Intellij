# Selenium Test Automation Framework

A comprehensive Page Object Model (POM) framework for web automation testing using Java, Selenium, TestNG, and ExtentReports.

## Features

- Page Object Model (POM) design pattern
- XPath-based element locators
- Cross-browser testing support
- Parallel test execution
- ExtentReports integration
- Screenshot capture on failure
- Retry mechanism for failed tests
- Docker support with Selenium Grid
- Configuration management
- Robust WebDriver actions

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Docker and Docker Compose (for Docker execution)

## Project Structure

```
src/test/java/
├── base/
│   ├── TestBase.java          # Base test class with WebDriver setup
│   └── WebDriverActions.java  # Reusable browser actions
├── pages/
│   └── VaneckPage.java        # Page Object for Vaneck website
├── tests/
│   └── VaneckTest.java        # Test class
├── utils/
│   ├── ConfigManager.java     # Configuration management
│   ├── ExtentManager.java     # ExtentReports management
│   ├── RetryAnalyzer.java     # Test retry mechanism
│   ├── ScreenshotUtil.java    # Screenshot capture utility
│   └── TestListener.java      # Test execution listener
└── config/
    └── BrowserConfig.java     # Browser configuration

src/test/resources/
├── config.properties          # Configuration properties
├── testng.xml                # TestNG configuration
├── docker/
│   ├── docker-compose.yml     # Selenium Grid configuration
│   ├── docker-compose.test.yml # Full test setup
│   ├── Dockerfile             # Test application container
│   └── .dockerignore          # Docker ignore file
├── scripts/
│   ├── run-tests-docker.sh    # Linux/Mac script
│   ├── run-tests-docker.bat   # Windows batch script
│   ├── docker-run.ps1         # PowerShell script
│   ├── run-docker-tests.sh    # Root-level Linux/Mac script
│   └── run-docker-tests.bat   # Root-level Windows script
└── docs/
    └── DOCKER_GUIDE.md        # Docker quick reference
```

## Running Tests

### Local Execution

1. **Run all tests:**
   ```bash
   mvn clean test
   ```

2. **Run with specific browser:**
   ```bash
   mvn clean test -Dbrowser=chrome
   mvn clean test -Dbrowser=firefox
   mvn clean test -Dbrowser=edge
   ```

3. **Run in headless mode:**
   ```bash
   mvn clean test -Dbrowser=chrome -DisHeadless=true
   ```

4. **Run with parallel execution:**
   ```bash
   mvn clean test -Dbrowser=chrome -Dparallel=true
   ```

### Docker Execution

#### Option 1: Using Scripts from Resources (Recommended)

1. **Windows (Batch):**
   ```bash
   src\test\resources\scripts\run-tests-docker.bat
   ```

2. **Windows (PowerShell):**
   ```bash
   powershell -ExecutionPolicy Bypass -File src\test\resources\scripts\docker-run.ps1
   ```

3. **Linux/Mac:**
   ```bash
   chmod +x src/test/resources/scripts/run-tests-docker.sh
   ./src/test/resources/scripts/run-tests-docker.sh
   ```

#### Option 2: Manual Docker Commands

1. **Start Selenium Grid:**
   ```bash
   docker-compose -f src/test/resources/docker/docker-compose.yml up -d
   ```

2. **Run tests with Docker:**
   ```bash
   docker-compose -f src/test/resources/docker/docker-compose.test.yml up --build test-app
   ```

3. **Run with specific browser:**
   ```bash
   docker-compose -f src/test/resources/docker/docker-compose.test.yml up --build test-app -e BROWSER=firefox
   ```

#### Option 3: Using Selenium Grid Only

1. **Start Selenium Grid:**
   ```bash
   docker-compose -f src/test/resources/docker/docker-compose.yml up -d
   ```

2. **Run tests locally against Docker Grid:**
   ```bash
   mvn clean test -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub -Dbrowser=chrome
   ```

## Configuration

### Environment Variables

- `env`: Environment (staging, production)
- `browser`: Browser type (chrome, firefox, edge)
- `isHeadless`: Run in headless mode (true/false)
- `use.grid`: Use Selenium Grid (true/false)
- `grid.url`: Selenium Grid URL
- `base.url`: Application base URL

### Docker Configuration

- **Selenium Hub**: `http://localhost:4444/wd/hub`
- **VNC Ports**: 
  - Chrome: `localhost:5900`
  - Firefox: `localhost:5901`
  - Edge: `localhost:5902`

## Test Reports

- **ExtentReports**: `test-output/reports/`
- **Screenshots**: `test-output/screenshots/`
- **TestNG Reports**: `test-output/`

## Docker Commands Reference

### Start Services
```bash
# Start all services
docker-compose -f src/test/resources/docker/docker-compose.yml up -d

# Start specific services
docker-compose -f src/test/resources/docker/docker-compose.yml up -d selenium-hub chrome

# Start with custom configuration
docker-compose -f src/test/resources/docker/docker-compose.test.yml up -d
```

### Stop Services
```bash
# Stop all services
docker-compose -f src/test/resources/docker/docker-compose.yml down

# Stop and remove volumes
docker-compose -f src/test/resources/docker/docker-compose.yml down -v
```

### View Logs
```bash
# View all logs
docker-compose -f src/test/resources/docker/docker-compose.yml logs

# View specific service logs
docker-compose -f src/test/resources/docker/docker-compose.yml logs selenium-hub
docker-compose -f src/test/resources/docker/docker-compose.yml logs chrome
```

### Access VNC (for debugging)
- Chrome: `vnc://localhost:5900`
- Firefox: `vnc://localhost:5901`
- Edge: `vnc://localhost:5902`

## Troubleshooting

### Common Issues

1. **Port conflicts**: Ensure ports 4442-4444, 5900-5902 are available
2. **Memory issues**: Increase Docker memory allocation
3. **Network issues**: Check Docker network connectivity
4. **Browser compatibility**: Ensure browser versions match

### Debug Mode

Enable debug mode in `config.properties`:
```properties
debug.mode=true
```

### Clean Up

```bash
# Remove all containers and images
docker-compose -f src/test/resources/docker/docker-compose.yml down --rmi all --volumes --remove-orphans

# Clean Docker system
docker system prune -a
```

## Documentation

- **Docker Guide**: `src/test/resources/docs/DOCKER_GUIDE.md` - Complete Docker reference
- **Scripts**: `src/test/resources/scripts/` - Ready-to-use execution scripts
- **Docker Files**: `src/test/resources/docker/` - All Docker configuration files

## Contributing

1. Follow the existing code structure
2. Add appropriate comments and documentation
3. Update tests and configuration as needed
4. Test thoroughly before submitting changes