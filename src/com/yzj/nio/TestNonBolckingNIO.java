package com.yzj.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Scanner;

public class TestNonBolckingNIO {

    @Test
    public void cilent() throws IOException {
        //获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        //切换成非阻塞模式
        sChannel.configureBlocking(false);
        //分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //发送数据
        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()) {
            String str = scan.next();
            buffer.put((LocalDateTime.now().toString() + "\n" + str).getBytes() );
            buffer.flip();
            sChannel.write(buffer);
            buffer.clear();
            sChannel.close();
        }
    }

    @Test
    public void server() throws IOException {
        // 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        //切换成非阻塞模式
        ssChannel.configureBlocking(false);
        //绑定链接
        ssChannel.bind(new InetSocketAddress(9898));
        //获取选择器
        Selector selector = Selector.open();
        //将通道注册到选择器,并且指定监听事件
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        //通过选择器轮巡的获取选择器上已经准备就绪的事件
        while (selector.select() > 0) {
            //获取当前选择器中所有注册的选择键（已就绪的监听事件）
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                //获取准备就绪的事件
                SelectionKey sk = it.next();
                //判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    //若 接受就绪 就获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();
                    //切换非阻塞模式
                    sChannel.configureBlocking(false);
                    //将该通道注册到选择器上
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    //获取当前选择器上读就绪状态的通道
                    SocketChannel sChannel = (SocketChannel) sk.channel();
                    //读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = sChannel.read(buffer)) > 0) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, len));
                        buffer.clear();
                    }
                }
                //取消选择键
                it.remove();
            }
        }

    }
}
