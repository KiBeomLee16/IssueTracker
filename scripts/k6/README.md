# Project Cache Load Test

This k6 test measures authenticated `GET /api/projects/{projectId}` performance with the same scenario before and after Redis caching.

## Scenario

- Warm up at 10 virtual users
- Ramp to 50 virtual users
- Ramp to 100 virtual users
- Hold 100 virtual users for 5 minutes
- One-second think time between requests

Thresholds:

- Request failure rate below 1%
- p95 below 500 ms
- p99 below 1,000 ms
- Check success rate above 99%

## Smoke Check

Start the Docker stack with Redis caching and run the short profile:

```bash
CACHE_TYPE=redis docker compose up -d --build app
CACHE_TYPE=redis K6_PROFILE=smoke docker compose --profile load-test run --rm k6
```

## Baseline Without Redis Caching

Redis still runs as a container, but Spring uses a no-op cache manager so every project lookup reaches MySQL.

```bash
CACHE_TYPE=none docker compose up -d --build --force-recreate app
CACHE_TYPE=none K6_PROFILE=load docker compose --profile load-test run --rm k6
```

Record p95, p99, request rate, and failure rate from the summary.

## Redis Cache Run

Recreate the app with Redis caching, clear old cache entries, and run the same load profile:

```bash
CACHE_TYPE=redis docker compose up -d --build --force-recreate app
docker compose exec redis redis-cli FLUSHDB
CACHE_TYPE=redis K6_PROFILE=load docker compose --profile load-test run --rm k6
```

Run each profile three times in the same environment and compare the median results.

## Overrides

The Compose service supports these environment variables:

- `K6_BASE_URL` (default `http://app:8080`)
- `K6_USER_ID` (default `admin01`)
- `K6_PASSWORD` (default `password`)
- `K6_PROJECT_ID` (default `1`)
- `K6_PROFILE` (`smoke` or `load`)
- `K6_THINK_TIME_SECONDS` (default `1`)
