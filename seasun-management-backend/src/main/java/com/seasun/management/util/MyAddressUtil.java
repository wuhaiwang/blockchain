package com.seasun.management.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.management.*;
import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

public class MyAddressUtil {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(MyAddressUtil.class);


    //获取本机ip
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // _logger.error("IP地址获取失败", e);
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取服务端口号
     *
     * @return 端口号
     * @throws ReflectionException
     * @throws MBeanException
     * @throws InstanceNotFoundException
     * @throws AttributeNotFoundException
     */
    public static String getServerPort(boolean secure) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
        MBeanServer mBeanServer = null;
        if (MBeanServerFactory.findMBeanServer(null).size() > 0) {
            mBeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
        }

        if (mBeanServer == null) {
            logger.debug("调用findMBeanServer查询到的结果为null");
            return "";
        }

        Set<ObjectName> names = null;
        try {
            names = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
        } catch (Exception e) {
            return "";
        }
        Iterator<ObjectName> it = names.iterator();
        ObjectName oname = null;
        while (it.hasNext()) {
            oname = (ObjectName) it.next();
            String protocol = (String) mBeanServer.getAttribute(oname, "protocol");
            String scheme = (String) mBeanServer.getAttribute(oname, "scheme");
            Boolean secureValue = (Boolean) mBeanServer.getAttribute(oname, "secure");
            Boolean SSLEnabled = (Boolean) mBeanServer.getAttribute(oname, "SSLEnabled");
            if (SSLEnabled != null && SSLEnabled) {// tomcat6开始用SSLEnabled
                secureValue = true;// SSLEnabled=true但secure未配置的情况
                scheme = "https";
            }
            if (protocol != null && ("HTTP/1.1".equals(protocol) || protocol.contains("http"))) {
                if (secure && "https".equals(scheme) && secureValue) {
                    return ((Integer) mBeanServer.getAttribute(oname, "port")).toString();
                } else if (!secure && !"https".equals(scheme) && !secureValue) {
                    return ((Integer) mBeanServer.getAttribute(oname, "port")).toString();
                }
            }
        }
        return "";
    }

    //获取访问ip
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
