# Docker Integration Guide

This directory contains the Docker configuration for running Selenium Grid with your test automation framework.

## File Structure

```
docker/
├── docker-compose.yml          # Base Grid configuration
├── docker-compose.dev.yml      # Development environment override
├── docker-compose.prod.yml     # Production environment override
└── README.md                   # This file
```

## Quick Start

### 1. Start Selenium Grid

**Interactive Mode:**
```bash
src\test\resources\scripts\start-grid.bat
```

**Direct Commands:**
```bash
# Development (reduced resources, debugging)
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Production (full resources, optimized)
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Base configuration
docker-compose -f docker-compose.yml up -d
```

### 2. Run Tests

**Interactive Mode:**
```bash
src\test\resources\scripts\run-tests.bat
```

**Direct Commands:**
```bash
# Single browser
mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=chrome -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub

# All browsers in parallel
src\test\resources\scripts\run-all-browsers.bat
```

### 3. Stop Grid

```bash
src\test\resources\scripts\stop-grid.bat
```

## Environment Configurations

### Development (`docker-compose.dev.yml`)
- **Purpose:** Local development and debugging
- **Features:**
  - Reduced session limits (2 per browser)
  - Shorter timeouts (180s)
  - Volume mounts for logs and downloads
  - Debugging enabled

### Production (`docker-compose.prod.yml`)
- **Purpose:** CI/CD and high-load environments
- **Features:**
  - Higher session limits (8 per browser)
  - Longer timeouts (600s)
  - Memory limits and reservations
  - Always restart policy

### Base (`docker-compose.yml`)
- **Purpose:** Default configuration
- **Features:**
  - Balanced settings
  - Health checks
  - Standard resource allocation

## Monitoring and Debugging

### Grid Console
- **URL:** http://localhost:4444/ui
- **Purpose:** Monitor active sessions and browser nodes

### VNC Access
- **Chrome:** `vnc://localhost:5900` (password: `secret`)
- **Firefox:** `vnc://localhost:5901` (password: `secret`)
- **Edge:** `vnc://localhost:5902` (password: `secret`)

### Logs
```bash
# View all logs
docker-compose -f docker-compose.yml logs

# View specific service logs
docker-compose -f docker-compose.yml logs selenium-hub
docker-compose -f docker-compose.yml logs chrome

# Follow logs in real-time
docker-compose -f docker-compose.yml logs -f
```

## Health Checks

All containers include health checks to ensure they're ready before accepting connections:

- **Hub:** Checks `/status` endpoint
- **Nodes:** Checks node status endpoint
- **Dependencies:** Nodes wait for hub to be healthy

## Resource Management

### Memory Allocation
- **Development:** 2GB shared memory per browser
- **Production:** 4GB memory limit, 2GB reservation per browser

### Session Limits
- **Development:** 2 sessions per browser node
- **Production:** 8 sessions per browser node
- **Base:** 4 sessions per browser node

## Troubleshooting

### Grid Not Starting
```bash
# Check container status
docker-compose -f docker-compose.yml ps

# Check logs
docker-compose -f docker-compose.yml logs selenium-hub

# Restart services
docker-compose -f docker-compose.yml restart
```

### Port Conflicts
```bash
# Check what's using port 4444
netstat -ano | findstr :4444

# Kill process or change ports in compose file
```

### Memory Issues
```bash
# Check Docker memory usage
docker stats

# Increase Docker Desktop memory limit
```

## Best Practices

1. **Use appropriate environment:** Development for local work, Production for CI/CD
2. **Monitor health checks:** Ensure containers are healthy before running tests
3. **Clean up regularly:** Use `stop-grid.bat` to clean up resources
4. **Check logs:** Monitor container logs for issues
5. **Use VNC for debugging:** Connect to browser containers for visual debugging

## CI/CD Integration

For CI/CD pipelines, use the production configuration:

```yaml
# Example GitHub Actions
- name: Start Selenium Grid
  run: |
    docker-compose -f src/test/resources/docker/docker-compose.yml -f src/test/resources/docker/docker-compose.prod.yml up -d

- name: Run Tests
  run: |
    mvn test -DsuiteXmlFile=testng1.xml -Dbrowser=chrome -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub

- name: Stop Grid
  run: |
    docker-compose -f src/test/resources/docker/docker-compose.yml down
``` 