#!/bin/bash

set -e

build_maven_project() {
  local dir="$1"
  echo "==="
  echo "Сборка проекта в $dir..."
  if [ -d "$dir" ]; then
    cd "$dir"
    if mvn clean package -DskipTests; then
      echo "Сборка проекта $dir успешна"
    else
      echo "Ошибка сборки проекта $dir"
      exit 1
    fi
    cd - > /dev/null
  else
    echo "Директория $dir не найдена!"
    exit 1
  fi
}

cleanup_docker_resources() {
  local services=("order")
  echo "==="
  echo "Остановка и удаление старых контейнеров и образов..."

  # Остановить и удалить контейнеры
  for service in "${services[@]}"; do
    if docker ps -a --format '{{.Names}}' | grep -q "^${service}$"; then
      echo "Остановка контейнера $service..."
      docker stop "$service" || true
      echo "Удаление контейнера $service..."
      docker rm "$service" || true
    fi
  done

  for image in "scrapper_image" "bot_image"; do
    if docker images -q "$image" > /dev/null 2>&1; then
      echo "Удаление образа $image..."
      docker rmi -f "$image" || true
    fi
  done
}

build_maven_project "order"

cleanup_docker_resources

echo "==="
echo "Запуск docker-compose..."
docker-compose up --build
