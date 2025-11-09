#!/bin/bash

# Script để build file .exe từ Compose Desktop
# Lưu ý: Để build .exe trên macOS, bạn cần JDK và công cụ cross-compilation

echo "Building TestDesktop application..."

# Build cho Windows (tạo file .exe)
./gradlew :composeApp:packageDistributionForCurrentOS

echo ""
echo "Build completed!"
echo "File .exe sẽ được tạo trong: composeApp/build/compose/binaries/main/distribution/"
echo ""
echo "Hoặc sử dụng các lệnh sau:"
echo "  ./gradlew :composeApp:packageDistributionForCurrentOS  - Build cho OS hiện tại"
echo "  ./gradlew :composeApp:packageUberJarForCurrentOS       - Build JAR file"
echo "  ./gradlew :composeApp:runDistributable                 - Chạy ứng dụng"

