#!/usr/bin/env bash
set -e

# Detect host architecture
HOST_ARCH=$(uname -m)
echo "🏠 Host architecture detected: $HOST_ARCH"

# Decide which platforms to build
BUILD_PLATFORMS=()

case "$HOST_ARCH" in
    x86_64)
        BUILD_PLATFORMS=("amd64" "arm64")  # can build both via buildx/QEMU
        ;;
    aarch64)
        BUILD_PLATFORMS=("arm64")           # only arm64 makes sense on M1
        ;;
    *)
        echo "❌ Unsupported host architecture: $HOST_ARCH"
        exit 1
        ;;
esac

for PLATFORM in "${BUILD_PLATFORMS[@]}"; do
    echo "🔧 Building for platform: $PLATFORM"

    docker buildx build \
        --platform linux/$PLATFORM \
        -f Dockerfile.native \
        --target export \
        --output type=local,dest=./build/$PLATFORM \
        -t volumio-nowplaying-remote:$PLATFORM .

    # Show actual binary architecture
    echo "Binary info for $PLATFORM:"
    file ./build/$PLATFORM/export/volumio-nowplaying-remote-*
done

echo "✅ All builds complete!"
