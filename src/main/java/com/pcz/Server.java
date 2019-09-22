package com.pcz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @author picongzhi
 */
public class Server {
    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = createServerSocket();
        initServerSocket(serverSocket);
        serverSocket.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);
        System.out.println("服务器启动成功...");
        System.out.println("服务器信息 IP: " + serverSocket.getInetAddress() + " 端口: " + serverSocket.getLocalPort());

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandler.start();
        }
    }

    private static ServerSocket createServerSocket() throws IOException {
//        ServerSocket serverSocket = new ServerSocket(PORT);

        // 连接队列长度设置为50
//        ServerSocket serverSocket = new ServerSocket(PORT, 50);

//        ServerSocket serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());

        return new ServerSocket();
    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {
        serverSocket.setReuseAddress(true);
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);
//        serverSocket.setSoTimeout(2000);
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("新客户端连接: " + socket.getInetAddress() + ":" + socket.getPort());
            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                byte[] buffer = new byte[128];
                int len = inputStream.read(buffer);


                if (len > 0) {
                    ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, len);
                    byte by = byteBuffer.get();
                    char c = byteBuffer.getChar();
                    int i = byteBuffer.getInt();
                    boolean b = byteBuffer.get() == 1;
                    long l = byteBuffer.getLong();
                    float f = byteBuffer.getFloat();
                    double d = byteBuffer.getDouble();
                    int pos = byteBuffer.position();
                    String str = new String(buffer, pos, len - pos - 1);

                    System.out.println("收到数据长度: " + len + " 数据: "
                            + by + "\n"
                            + c + "\n"
                            + i + "\n"
                            + b + "\n"
                            + l + "\n"
                            + f + "\n"
                            + d + "\n"
                            + str);
                    outputStream.write(buffer, 0, len);
                } else {
                    System.out.println("没有收到数据");
                    outputStream.write(new byte[]{0});
                }

                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
