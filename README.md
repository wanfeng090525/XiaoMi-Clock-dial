# 🔧 小米表盘 ID 修改工具

> 一款用于 **修改小米手环 / 手表表盘文件中的 WatchFace ID**，并支持 **提取小米账号 Token 与设备 AuthKey** 的轻量级桌面工具。
> 
> ✨ 通过替换官方已下载表盘的 ID，绕过 Mi Fitness (小米运动健康) 的校验，实现第三方自定义表盘的安装。

![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20Linux%20%7C%20Android-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Release](https://img.shields.io/github/v/release/【你的用户名】/【你的仓库名】)

---

## 📖 项目简介

【在此补充 1–3 句话的项目背景】

本项目通过解析小米 `.face` / `.bin` 表盘包的二进制结构，定位并重写其中的 **WatchFace ID 字段**，
使其可以伪装成官方已下载过的某个正版表盘，从而被小米运动健康 App 正常同步到设备上；
同时集成了 Token 提取模块，可从本地数据库或云端接口中读取到当前账号绑定的 **AuthKey / MAC 地址**，
供 Gadgetbridge、Suiteki 等第三方客户端使用。

## 🚀 核心功能

- ✅ **表盘 ID 一键修改** — 自动扫描 `.face` / `.bin` 文件，找到 ID 偏移位置并覆写为新值。
- ✅ **Token / AuthKey 提取** — 支持从小米运动健康 (Mi Fitness) 数据库或小米云端 API 提取蓝牙配对密钥。
- ✅ **多设备兼容** — 覆盖小米手环 7/8/9、手表 S3/S4 以及 Redmi Watch 系列。【按实际情况增删】
- ✅ **GUI 可视化界面** — 图形化操作，无需记忆任何命令行参数。
- ✅ **批量处理** — 支持拖入多个文件一次完成 ID 替换。

## 📱 已测试支持的设备

| 设备型号 | 固件版本 | 是否测试通过 |
|---|---|---|
| Xiaomi Smart Band 8 Pro | 【例：1.4.213】 | ✅ |
| Xiaomi Watch S3 | 【】 | ✅ |
| Redmi Watch 4 | 【】 | ⚠️ 部分功能 |
| 【其他设备】 | 【】 | ❓ 待验证 |

> 如果你在其他机型上使用成功或遇到问题，欢迎提交 Issue 反馈。

## 🛠️ 环境要求

- 【Windows 10 / 11 x64】 或 【Python ≥ 3.9】 或 【Android 8.0+】 —— 三选一按项目类型保留
- 已安装 **小米运动健康 (Mi Fitness)** 且登录过小米账号（用于 Token 提取）
- 【可选】Shizuku 授权 或 `Android/data` 访问权限

## 📦 安装方式

### 方式一：下载可执行文件（推荐）
1. 前往 [Releases](https://github.com/【你的用户名】/【你的仓库名】/releases) 页面下载最新版压缩包。
2. 解压后双击 `【主程序.exe】` 即可运行，无需安装额外依赖。

### 方式二：源码运行
