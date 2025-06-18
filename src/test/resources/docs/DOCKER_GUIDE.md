# Docker Quick Reference Guide

## Quick Start Commands

### 1. Start Selenium Grid Only
```bash
docker-compose -f src/test/resources/docker/docker-compose.yml up -d
```

### 2. Run Tests with Docker (Full Setup)
```bash
# Windows
src\test\resources\scripts\run-tests-docker.bat
# or
powershell -ExecutionPolicy Bypass -File src\test\resources\scripts\docker-run.ps1

# Linux/Mac
chmod +x src/test/resources/scripts/run-tests-docker.sh
./src/test/resources/scripts/run-tests-docker.sh
```

### 3. Run Tests Locally Against Docker Grid
```bash
mvn clean test -Duse.grid=true -Dgrid.url=http://localhost:4444/wd/hub -Dbrowser=chrome
```

## Docker Compose Files

- `src/test/resources/docker/docker-compose.yml` - Selenium Grid only
- `src/test/resources/docker/docker-compose.test.yml` - Full setup with test application

## Useful Commands

### Check Container Status
```bash
docker-compose -f src/test/resources/docker/docker-compose.yml ps
```

### View Logs
```bash
# All services
docker-compose -f src/test/resources/docker/docker-compose.yml logs

# Specific service
docker-compose -f src/test/resources/docker/docker-compose.yml logs selenium-hub
docker-compose -f src/test/resources/docker/docker-compose.yml logs chrome
docker-compose -f src/test/resources/docker/docker-compose.test.yml logs test-app

# Follow logs in real-time
docker-compose -f src/test/resources/docker/docker-compose.yml logs -f selenium-hub
```

### Access Selenium Grid Console
- Open browser: `http://localhost:4444/ui`

### VNC Access (for debugging)
- Chrome: `vnc://localhost:5900` (password: `secret`)
- Firefox: `vnc://localhost:5901` (password: `secret`)
- Edge: `vnc://localhost:5902` (password: `secret`)

### Stop Services
```bash
# Stop all
docker-compose -f src/test/resources/docker/docker-compose.yml down

# Stop and remove volumes
docker-compose -f src/test/resources/docker/docker-compose.yml down -v

# Stop and remove images
docker-compose -f src/test/resources/docker/docker-compose.yml down --rmi all
```

## Troubleshooting

### Port Already in Use
```bash
# Check what's using the port
netstat -ano | findstr :4444

# Kill the process or change ports in docker-compose.yml
```

### Container Won't Start
```bash
# Check logs
docker-compose -f src/test/resources/docker/docker-compose.yml logs selenium-hub

# Restart services
docker-compose -f src/test/resources/docker/docker-compose.yml restart

# Rebuild containers
docker-compose -f src/test/resources/docker/docker-compose.yml up --build
```

### Network Issues
```bash
# Check Docker networks
docker network ls

# Inspect network
docker network inspect selenium_default
```

### Memory Issues
```bash
# Check Docker memory usage
docker stats

# Increase Docker memory in Docker Desktop settings
```

### Clean Up Docker
```bash
# Remove unused containers, networks, images
docker system prune

# Remove everything (use with caution)
docker system prune -a --volumes
```

## Environment Variables

You can override these in `src/test/resources/docker/docker-compose.test.yml`:

```yaml
environment:
  - BROWSER=chrome          # chrome, firefox, edge
  - IS_HEADLESS=true        # true, false
  - BASE_URL=https://www.vaneck.com
  - USE_GRID=true
  - GRID_URL=http://selenium-hub:4444/wd/hub
```

## Custom Test Execution

### Run Specific Test Class
```bash
docker run --network selenium_default -v $(pwd)/test-output:/app/test-output selenium-test-framework ./mvnw test -Dtest=VaneckTest
```

### Run with Different Browser
```bash
docker run --network selenium_default -v $(pwd)/test-output:/app/test-output selenium-test-framework ./mvnw test -Dbrowser=firefox
```

### Run in Parallel
```bash
docker run --network selenium_default -v $(pwd)/test-output:/app/test-output selenium-test-framework ./mvnw test -Dparallel=true
```

## Performance Tips

1. **Use headless mode** for faster execution
2. **Increase shm_size** if you encounter memory issues
3. **Use parallel execution** for multiple browsers
4. **Mount test-output directory** to preserve results
5. **Use specific browser nodes** instead of all browsers

## Security Notes

- VNC passwords are set to 'secret' by default
- Consider changing passwords for production use
- Expose only necessary ports
- Use Docker secrets for sensitive data 