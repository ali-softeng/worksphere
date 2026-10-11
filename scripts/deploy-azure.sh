```bash
#!/usr/bin/env bash
set -Eeuo pipefail

COMPOSE_FILE="/home/ali/worksphere-source/compose.yaml"
ENV_FILE="/etc/worksphere/worksphere.env"
EXPECTED_IMAGE="ghcr.io/ali-softeng/worksphere"

if [[ ! "${IMAGE_TAG:-}" =~ ^[0-9a-f]{40}$ ]]; then
  echo "ERROR: Invalid or missing IMAGE_TAG."
  exit 1
fi

export WORKSPHERE_IMAGE_TAG="$IMAGE_TAG"

compose() {
  sudo --preserve-env=WORKSPHERE_IMAGE_TAG docker compose \
    --env-file "$ENV_FILE" \
    -f "$COMPOSE_FILE" "$@"
}

check_health() {
  local response

  for _ in $(seq 1 30); do
    if response=$(curl -fsS http://localhost/actuator/health 2>/dev/null) &&
       printf '%s' "$response" |
         grep -Eq '"status"[[:space:]]*:[[:space:]]*"UP"'; then
      return 0
    fi

    sleep 2
  done

  return 1
}

echo "Identifying the current backend image..."

container_id=$(compose ps -q backend)

if [[ -z "$container_id" ]]; then
  echo "ERROR: No current backend container found."
  exit 1
fi

current_image=$(sudo docker inspect \
  --format='{{.Config.Image}}' "$container_id")

case "$current_image" in
  "$EXPECTED_IMAGE":*) ;;
  *)
    echo "ERROR: Unexpected current image: $current_image"
    exit 1
    ;;
esac

previous_tag="${current_image##*:}"

if [[ ! "$previous_tag" =~ ^[0-9a-f]{40}$ ]]; then
  echo "ERROR: Invalid previous image tag: $previous_tag"
  exit 1
fi

echo "Previous image: $previous_tag"
echo "Target image:   $IMAGE_TAG"

rollback() {
  echo "Attempting rollback to $previous_tag..."
  export WORKSPHERE_IMAGE_TAG="$previous_tag"

  if ! compose up -d --no-deps backend; then
    echo "ERROR: Rollback command failed."
    compose logs --tail=100 backend || true
    return 1
  fi

  if check_health; then
    echo "Rollback successful; previous backend is healthy."
    return 0
  fi

  echo "ERROR: Previous backend failed its health check."
  compose logs --tail=100 backend || true
  return 1
}

echo "Pulling the new backend image..."

if ! compose pull backend; then
  echo "ERROR: Image pull failed. Existing backend was not intentionally changed."
  exit 1
fi

echo "Deploying the new backend..."

if ! compose up -d --no-deps backend; then
  echo "ERROR: Deployment command failed."
  rollback || true
  exit 1
fi

echo "Checking the new backend health..."

if check_health; then
  echo "SUCCESS: New backend is healthy."
  echo "Deployed image: $IMAGE_TAG"
  exit 0
fi

echo "ERROR: New backend did not become healthy."
compose logs --tail=100 backend || true

if rollback; then
  echo "Previous release restored. Deployment is still considered failed."
else
  echo "CRITICAL: Automatic rollback could not restore a healthy backend."
fi

exit 1
```
