package com.yzj.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 使用NIO完成网络通信的三个核心：
 * 1 通道 Channel : 负责连接
 * java.nio.channels.Channel 接口：
 * |--SelectableChannel
 * |---SocketChannel
 * |---ServerSocketChannel
 * |---Datagramchannel
 * |---Pipe.SinkChannel
 * |---Pipe.SourceChannel
 * <p>
 * 2 缓冲区(Buffer):负责数据的存取
 * 3 选择器(Selector):是 SelectableChannel 的多路复用，用来监控
 * SelectableChannel的一些IO状况的
 */
@SuppressWarnings("Duplicates")
public class TestBlockingNIO {

    @Test
    public void cilent() throws IOException {
        //1 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannel = FileChannel.open(Paths.get("/home/yangzhijie/下载/idea.txt"), StandardOpenOption.READ);

        //2 分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        //3 读取本地文件，并发送到服务端去
        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            sChannel.write(buffer);
            buffer.clear();
        }
        inChannel.close();
        sChannel.close();
    }

    @Test
    public void server() throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.bind(new InetSocketAddress(9898));
        SocketChannel sChannel = ssChannel.accept();
        FileChannel outChannel = FileChannel.open(Paths.get("/home/yangzhijie/下载/idea3.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (sChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }
        sChannel.close();
        ssChannel.close();
        outChannel.close();
    }
}
