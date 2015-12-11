package me.faolou.learnzookeeper.custom;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author zhangwenbin
 * @since 2015/10/21.
 */
public final class SystemUtils {

    public static String findLocalAddressIp() throws SocketException {
        //此类表示一个由名称和分配给此接口的 IP 地址列表组成的网络接口
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        //遍历
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                InetAddress ip = ips.nextElement();
                if (ip.isLoopbackAddress()) {
                    continue;
                }
                if (!ip.getHostAddress().contains(":")) {
                    //获得Ip地址
                    return ip.getHostAddress();
                }
            }
        }
        return null;
    }
}
