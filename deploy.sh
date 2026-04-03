#!/bin/bash

source /home/ubuntu/travel-project/.env

DOCKER_USERNAME="dangyee"

EXIST_BLUE=$(docker ps | grep travel-app-blue)

if [ -z "$EXIST_BLUE" ]; then
    TARGET_PORT=8081
    TARGET_COLOR="blue"
    STOP_PORT=8082
    STOP_COLOR="green"
else
    TARGET_PORT=8082
    TARGET_COLOR="green"
    STOP_PORT=8081
    STOP_COLOR="blue"
fi

echo "> $TARGET_COLOR 배포 시작 (Port: $TARGET_PORT)"

echo "> 기존 $TARGET_COLOR 잔해 삭제 중..."
docker rm -f travel-app-$TARGET_COLOR 2>/dev/null || true

docker pull $DOCKER_USERNAME/travel-app:latest
docker run -d --name travel-app-$TARGET_COLOR \
    --network travel-network \
    -p $TARGET_PORT:8080 \
    -v /home/ubuntu/travel-project/uploads:/app/uploads \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://travel-db:5432/traveldb \
    -e SPRING_DATASOURCE_USERNAME=$POSTGRES_USER \
    -e SPRING_DATASOURCE_PASSWORD=$POSTGRES_PASSWORD \
    $DOCKER_USERNAME/travel-app:latest

IS_SUCCESS=false
for retry in {1..10}
do
    echo "> Health check 중... ($retry)"
    RESPONSE=$(curl -s http://localhost:$TARGET_PORT/api/v1/questions)
    if [ -n "$RESPONSE" ]; then
        echo "> Health check 성공! 정상 가동 확인."
        IS_SUCCESS=true
        break
    fi
    sleep 5
done

if [ "$IS_SUCCESS" = false ]; then
    echo "> 🚨 Health check 실패! 새 버전에 문제가 있습니다."
    echo "> 기존 $STOP_COLOR 컨테이너를 유지하고, 실패한 $TARGET_COLOR 컨테이너를 삭제합니다."
    docker stop travel-app-$TARGET_COLOR || true
    docker rm travel-app-$TARGET_COLOR || true
    exit 1
fi

echo "set \$service_url http://travel-app-$TARGET_COLOR:8080;" | sudo tee /home/ubuntu/travel-project/nginx/conf.d/service-url.inc
docker exec travel-nginx nginx -s reload

docker stop travel-app-$STOP_COLOR || true
docker rm travel-app-$STOP_COLOR || true

echo "> 🎉 무중단 배포 완료!"
