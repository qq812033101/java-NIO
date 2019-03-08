package com.yzj.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@SuppressWarnings("Duplicates")
public class TestBlockingNIO2 {
    @Test
    public void client() throws IOException {
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));
        ByteBuffer buf = ByteBuffer.allocate(1024);
        FileChannel inChannel = FileChannel.open(Paths.get("/home/yangzhijie/下载/idea3.txt"), StandardOpenOption.READ);
        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.flip();
        }

        sChannel.shutdownOutput();

        int len = 0;
        while ((len = sChannel.read(buf)) != -1) {
            buf.flip();
            System.out.println(new String(buf.array(), 0, len));
            buf.clear();
        }

        sChannel.close();
        inChannel.close();
    }


    @Test
    public void server() throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();
        ssChannel.bind(new InetSocketAddress(9898));
        SocketChannel sChannel = ssChannel.accept();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        FileChannel outChannel = FileChannel.open(Paths.get("/home/yangzhijie/下载/idea4.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        while (sChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }

        //发送反馈给客户端
        buffer.put("服务端接受客户端成功".getBytes());
        buffer.flip();
        sChannel.write(buffer);
        ssChannel.close();
        sChannel.close();
        outChannel.close();


    }
}
