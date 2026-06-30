# Local Kubernetes Run

This directory contains a minimal local Kubernetes deployment for the issue tracker API:

- Spring Boot API
- MySQL 8.0 with a persistent volume claim
- Redis 7 cache
- ConfigMap and Secret-based environment configuration

The manifests are intended for local practice with Docker Desktop Kubernetes, kind, or minikube.

## 0. Enable a local Kubernetes cluster

Docker Desktop:

1. Open Docker Desktop.
2. Go to Settings > Kubernetes.
3. Check "Enable Kubernetes".
4. Apply and restart.

After Kubernetes starts, verify the context:

```bash
kubectl config get-contexts
kubectl config use-context docker-desktop
kubectl cluster-info
```

If `kubectl config get-contexts` shows no contexts, Kubernetes is not enabled yet.

kind alternative:

```bash
kind create cluster --name issue-tracker
kubectl config use-context kind-issue-tracker
```

## 1. Build the local image

```bash
docker build -t issue-tracker-api:local .
```

Docker Desktop Kubernetes can use this image directly. If you use kind, load the image into the cluster:

```bash
kind load docker-image issue-tracker-api:local
```

## 2. Prepare secrets

For a quick local demo, you can apply `secret.example.yaml` directly. For a real environment, copy it and edit the values:

```bash
cp k8s/secret.example.yaml k8s/secret.yaml
```

`k8s/secret.yaml` is ignored by Git.

## 3. Apply Kubernetes manifests

Quick local demo:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.example.yaml
kubectl apply -f k8s/mysql.yaml
kubectl apply -f k8s/redis.yaml
kubectl apply -f k8s/app.yaml
```

If you created `k8s/secret.yaml`, apply that instead of `secret.example.yaml`.

## 4. Wait for the pods

```bash
kubectl -n issue-tracker get pods -w
```

Or check rollouts individually:

```bash
kubectl -n issue-tracker rollout status deploy/mysql
kubectl -n issue-tracker rollout status deploy/redis
kubectl -n issue-tracker rollout status deploy/issue-tracker-api
```

## 5. Open the API locally

```bash
kubectl -n issue-tracker port-forward svc/issue-tracker-api 8080:8080
```

Then open:

- http://localhost:8080/actuator/health
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/demo/index.html

Demo accounts:

| Role | User ID | Password |
| --- | --- | --- |
| Admin | `admin01` | `password` |
| Owner | `owner01` | `password` |
| Member | `member01` | `password` |

## 6. Run the smoke test

In another terminal:

```bash
powershell.exe -ExecutionPolicy Bypass -File ./scripts/smoke-test-prod.ps1
```

## Useful commands

```bash
kubectl -n issue-tracker get all
kubectl -n issue-tracker logs deploy/issue-tracker-api
kubectl -n issue-tracker describe pod -l app=issue-tracker-api
kubectl -n issue-tracker exec deploy/redis -- redis-cli keys '*'
```

## Troubleshooting

If `kubectl` tries to connect to `localhost:8080` and fails, the local Kubernetes context is not configured. Enable Docker Desktop Kubernetes or create a kind cluster, then run `kubectl config get-contexts` again.

If the API pod is stuck in `ImagePullBackOff`, the cluster cannot see `issue-tracker-api:local`.

- Docker Desktop: rebuild with `docker build -t issue-tracker-api:local .`.
- kind: run `kind load docker-image issue-tracker-api:local`.

If the API pod restarts while MySQL is initializing, wait for MySQL to become ready:

```bash
kubectl -n issue-tracker rollout status deploy/mysql
kubectl -n issue-tracker rollout restart deploy/issue-tracker-api
```

## Reset local Kubernetes resources

This deletes the API, Redis, MySQL, and the MySQL volume claim:

```bash
kubectl delete namespace issue-tracker
```
