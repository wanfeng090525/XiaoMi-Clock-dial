#!/usr/bin/env bash
# ============================================================
# 一键构建 WatchfaceIdTool(小米表盘 ID 工具)Release APK
# 用法:./build.sh
# 前提:已安装 JDK 17+ (推荐 21) 和 Android SDK
#       若未设 ANDROID_HOME,脚本会自动探测常见 SDK 路径
# ============================================================
set -euo pipefail
cd "$(dirname "$0")"

echo "==> [1/3] 定位 Android SDK"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$SDK" ] || [ ! -d "$SDK" ]; then
  for c in "$HOME/Android/Sdk" /opt/android-sdk /usr/local/android-sdk "$HOME/Library/Android/sdk"; do
    if [ -d "$c" ]; then SDK="$c"; break; fi
  done
fi
if [ -z "${SDK:-}" ] || [ ! -d "$SDK" ]; then
  echo "错误: 找不到 Android SDK。" >&2
  echo "请安装 Android SDK 并执行: export ANDROID_HOME=/path/to/android-sdk" >&2
  exit 1
fi
echo "      使用 SDK: $SDK"

echo "==> [2/3] 写入 local.properties (sdk.dir=$SDK)"
echo "sdk.dir=$SDK" > local.properties

echo "==> [3/3] 执行 Gradle 构建 (assembleRelease, 已内置信签名)"
./gradlew --no-daemon assembleRelease

echo ""
echo "=========================================="
echo "构建成功!APK 位于:"
find app/build/outputs/apk -name "*.apk" -type f 2>/dev/null
echo "=========================================="