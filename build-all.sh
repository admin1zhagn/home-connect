#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo "[1/3] Building frontend..."
( cd frontend && npm install && npm run build )
echo "[2/3] Copying frontend dist into backend static..."
mkdir -p backend/src/main/resources/static
cp -r frontend/dist/* backend/src/main/resources/static/
echo "[3/3] Packaging backend jar..."
( cd backend && ./mvnw clean package -DskipTests )
echo "Done. Run: java -jar backend/target/home-connect-service-1.0.0.jar"
