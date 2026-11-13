#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

if ! command -v docker >/dev/null 2>&1; then
  echo "Docker no esta instalado: https://docs.docker.com/get-docker/" >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Docker Compose v2 no esta disponible." >&2
  exit 1
fi

if [ ! -f .env ]; then
  cp .env.example .env
  echo "Se creo .env desde .env.example. Edita los secretos y ejecuta de nuevo." >&2
  exit 1
fi

docker compose up -d --build
docker compose ps

echo
echo "Frontend:        http://localhost:3000"
echo "Auth API:        http://localhost:9090"
echo "Catalogo API:    http://localhost:9092"
echo "Mailpit:         http://localhost:8025"
echo "RabbitMQ:        http://localhost:15672"
