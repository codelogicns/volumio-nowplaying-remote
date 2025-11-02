#!/usr/bin/env bash
set -e

# Build Docker image
docker build --platform=linux/amd64 -f Dockerfile.native -t my-native-build .

# Extract native executable to target/
mkdir -p build

docker create --name tempbuild my-native-build
docker cp tempbuild:/workspace/target/ ./build
docker rm tempbuild

echo "✅ Native image available in build/ directory"