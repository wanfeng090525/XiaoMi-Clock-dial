package com.watchface.idtool

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import java.net.NetworkInterface

/**
 * VPN 检测工具
 * 检测系统是否正在使用 VPN 连接（包括虚拟网卡、VPN 应用等）
 */
object VpnDetector {

    /**
     * 检测是否使用 VPN
     * 返回 true 表示检测到 VPN，建议退出应用
     */
    fun isVpnActive(context: Context): Boolean {
        return checkNetworkInterfaces() || checkConnectivityManager(context)
    }

    /**
     * 检查网络接口中是否存在 VPN 相关设备
     */
    private fun checkNetworkInterfaces(): Boolean {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            interfaces.asSequence()
                .map { it.name.lowercase() }
                .any { name ->
                    // 常见 VPN 接口名称
                    name.startsWith("tun") ||      // TUN/TAP 设备
                    name.startsWith("ppp") ||      // PPP 连接
                    name.startsWith("vpn") ||      // VPN 设备
                    name.startsWith("vtun") ||     // Virtual TUN
                    name.startsWith("utun") ||     // macOS-style TUN
                    name == "lo0" && isLoopbackVpn(name) ||
                    name.contains("wireguard") ||  // WireGuard
                    name.contains("openvpn") ||    // OpenVPN
                    name.contains("clash") ||      // Clash VPN
                    name.contains("v2ray") ||      // V2Ray
                    name.contains("sing-box") ||   // Sing-box
                    name.contains("shadowsocks")   // Shadowsocks
                }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 通过 ConnectivityManager 检查 VPN 网络
     */
    private fun checkConnectivityManager(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                    ?: return false
                val capabilities = cm.getNetworkCapabilities(network)
                    ?: return false
                
                // 检查是否是 VPN 网络
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            } else {
                @Suppress("DEPRECATION")
                val info = cm.activeNetworkInfo
                info?.type == ConnectivityManager.TYPE_VPN
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 检查是否是 VPN 相关的 loopback
     */
    private fun isLoopbackVpn(interfaceName: String): Boolean {
        // 某些 VPN 应用会使用特殊的 loopback 接口
        return false // 简化处理，loopback 一般不认为是 VPN
    }
}
