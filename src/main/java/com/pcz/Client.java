package com.pcz;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author picongzhi
 */
public class Client {
    private static final int REMOTE_PORT = 8888;
    private static final int LOCAL_PORT = 9999;

    public static void main(String[] args) throws IOException {
        Socket socket = createSocket();
        initSocket(socket);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), REMOTE_PORT), 3000);
        System.out.println("服务器连接成功...");
        System.out.println("客户端信息 IP: " + socket.getLocalAddress() + " 端口: " + socket.getLocalPort());
        System.out.println("服务端信息 IP: " + socket.getInetAddress() + " 端口: " + socket.getPort());

        try {
            todo(socket);
        } catch (Exception e) {
            System.out.println("异常关闭");
        }

        socket.close();
        System.out.println("客户端已关闭");
    }

    private static Socket createSocket() throws IOException {
        // 无代理模式
//         Socket socket = new Socket(Proxy.NO_PROXY);

        // 代理模式
//        Proxy proxy = new Proxy(Proxy.Type.HTTP,
//                new InetSocketAddress(Inet4Address.getByAddress(new byte[] {1,1,1,1}), 8388));
//        Socket socket = new Socket(proxy);

        // 新建套接字并且连接到IP端口
//        Socket socket = new Socket("localhost", REMOTE_PORT);

        // 新建套接字并且连接到IP端口
//        Socket socket = new Socket(Inet4Address.getLocalHost(), REMOTE_PORT);

        // 新建套接字连接到IP端口，并且绑定到本地端口
//        Socket socket = new Socket("localhost", REMOTE_PORT, Inet4Address.getLocalHost(), LOCAL_PORT);
//        Socket socket = new Socket(Inet4Address.getLocalHost(), REMOTE_PORT, Inet4Address.getLocalHost(), LOCAL_PORT);

        Socket socket = new Socket();
        socket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), LOCAL_PORT));

        return socket;
    }

    private static void initSocket(Socket socket) throws SocketException {
        // 设置读取超时时间
        socket.setSoTimeout(2000);
        // 是否复用未完全关闭的Socket地址，对于制定bind操作后的套接字有效
        socket.setReuseAddress(true);
        // 是否开启Nagle算法
        socket.setTcpNoDelay(true);
        // 是否需要在长时间无数据传输发送确认数据（类似心跳包），时间大约为2小时
        socket.setKeepAlive(true);
        // 对于close关闭操作时进行怎么的处理，默认为false, 0
        // 1. false, 0: 默认，关闭时立即返回，底层系统接管输出流，将缓冲区内的数据发送完成
        // 2. true, 0: 关闭时立即返回，缓冲区数据抛弃，直接发送RST结束命令给对方，并无需经过2MSL等待
        // 3. true, 200: 关闭时最长阻塞200毫秒，随后按第二种情况处理
        socket.setSoLinger(true, 20);
        // 是否让紧急数据内敛，默认为false，紧急数据通过socket.sendUrgentData(1);发送
        socket.setOOBInline(true);
        // 设置接收发送缓冲区大小
        socket.setReceiveBufferSize(64 * 1024 * 1024);
        socket.setSendBufferSize(64 * 1024 * 1024);
        // 设置性能参数：短连接，延迟，带宽的相对重要性
        socket.setPerformancePreferences(1, 1, 0);
    }

    private static void todo(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        byte[] input = new byte[128];
        ByteBuffer byteBuffer = ByteBuffer.wrap(input);
        // byte
        byteBuffer.put((byte) 126);
        // char
        byteBuffer.putChar('a');
        // int
        byteBuffer.putInt(123456);
        // boolean
        boolean b = true;
        byteBuffer.put(b ? (byte) 1 : (byte) 0);
        // long
        byteBuffer.putLong(123456789L);
        // float
        byteBuffer.putFloat(123.456f);
        // double
        byteBuffer.putDouble(123.456);
        // string
        byteBuffer.put("hello world".getBytes());
        outputStream.write(input, 0, byteBuffer.position() + 1);

        byte[] buffer = new byte[128];
        int len = inputStream.read(buffer);

        if (len > 0) {
            System.out.println("收到数据长度: " + len);
        } else {
            System.out.println("没有收到数据");
        }

        outputStream.close();
        inputStream.close();
    }
}
