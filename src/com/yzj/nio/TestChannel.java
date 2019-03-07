package com.yzj.nio;

import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * 1 通道(Channel):负责连接源节点跟目标节点（铁路）。在JAVA NIO主要负责缓冲区数据的传输
 * Channel 本身不存储任何的数据的，因此需要配合缓冲区进行传输。
 * <p>
 * 2 通道的一些主要实现类
 * java.nio.channels.Channel接口
 * |--FileChannel //完成本地文件数据传输
 * |--SocketChannel
 * |--ServerSocketChannel
 * |--DatagramChannel
 * <p>
 * 3 获取通道
 * 3.1 Java针对支持通道的类提供了getChannel()方法
 * 本地io:
 * FileInputStram/FileOutputStream
 * RandomAccessFile//随机存储文件流
 * 网络io:
 * Socket
 * ServerSocket
 * DatagramSocket
 * 3.2 在 JDK 1.7 中的 NIO.2针对各个通道提供了一个静态方法 open()
 * 3.3 在 JDK 1.7 中的 NIO.2 的 Files 工具类的 new ByteChannel() 方法
 * <p>
 * 4 通道之间的数据传输
 * transferTo()
 * transferTo()
 * <p>
 * 5 分散(Scatter)与聚集(Gather)
 * 分散读取（Scatter Reads)：将通道中的数据分散到各个缓冲区中去
 * 聚集写入(Gathering Writes):将多个缓冲区中的数据都聚集到缓冲区中
 * <p>
 * 6 字符集：Charset
 * 编码：字符串->字节数组
 * 解码：字节数组->字符串
 */
@SuppressWarnings({"unused", "Duplicates"})
public class TestChannel {
    //1 .利用通道文成文件的复制（非直接缓冲区)
    @Test
    public void test1() throws IOException {
        //2000起
        long start = System.currentTimeMillis();
        FileInputStream fis = new FileInputStream("/home/yangzhijie/下载/idea.zip");
        FileOutputStream fos = new FileOutputStream("/home/yangzhijie/下载/idea3.zip");
        //获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();
        //分配指定大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //将通道中的数据存入缓冲区中
        while (inChannel.read(buffer) != -1) {
            //将缓冲区中的数据在写入通道中
            buffer.flip();//切换成读取数据的模式
            outChannel.write(buffer);
            buffer.clear();//情况缓冲区
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();

    }

    //2使用直接缓冲区(内存映射文件的方式)
    @Test
    public void test2() throws IOException {
        //1500起
        long start = System.currentTimeMillis();
        FileChannel innChannel = FileChannel
                .open(Paths.get("/home/yangzhijie/下载/idea.zip"), new StandardOpenOption[]{StandardOpenOption.READ});
        FileChannel outChannel = FileChannel
                .open(Paths.get("/home/yangzhijie/下载/idea2.zip"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

        MappedByteBuffer inMapperByteBuffer = innChannel.map(FileChannel.MapMode.READ_ONLY, 0, innChannel.size());
        MappedByteBuffer outMappedByteBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, innChannel.size());

        byte[] dst = new byte[inMapperByteBuffer.limit()];
        inMapperByteBuffer.get(dst);
        outMappedByteBuffer.put(dst);
        innChannel.close();
        outChannel.close();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    @Test
    public void test3() throws IOException {
        //这里使用的也是直接缓冲区的方式
        FileChannel innChannel = FileChannel
                .open(Paths.get("/home/yangzhijie/下载/idea.zip"), new StandardOpenOption[]{StandardOpenOption.READ});
        FileChannel outChannel = FileChannel
                .open(Paths.get("/home/yangzhijie/下载/idea2.zip"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
        long l = innChannel.transferTo(0, innChannel.size(), outChannel);
        long l1 = outChannel.transferFrom(innChannel, 0, innChannel.size());
        System.out.println(l1);
        System.out.println(l);
        innChannel.close();
        outChannel.close();
    }


    @Test
    public void test4() throws IOException {
        //分散 聚集
        RandomAccessFile raf1 = new RandomAccessFile("/home/yangzhijie/下载/idea.txt", "rw");
        //1 获取通道
        FileChannel channel1 = raf1.getChannel();
        //2 分配指定大小的缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);
        ByteBuffer buf3 = ByteBuffer.allocate(100);
        //3 分散读取
        ByteBuffer[] bufs = {buf1, buf2, buf3};
        long read = channel1.read(bufs);
        for (ByteBuffer buffer : bufs) {
            buffer.flip();
        }
        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("-----------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("/home/yangzhijie/下载/idea2.txt", "rw");
        FileChannel channel2 = raf2.getChannel();
        channel2.write(bufs);
    }


    //字符集
    @Test
    public void test5() {
        Map<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> set = map.entrySet();
        for (Map.Entry<String, Charset> entry : set) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }

    @Test
    public void test6() throws CharacterCodingException {
        Charset cs1 = Charset.forName("GBK");
        //获取编码器跟解码器
        CharsetEncoder ce = cs1.newEncoder();//编码器
        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();//解码器
        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("尚硅谷威武");
        cBuf.flip();
        //编码
        ByteBuffer bBuf = ce.encode(cBuf);
        for (int i = 0; i < bBuf.capacity(); i++) {
            System.out.println(bBuf.get(i));
        }
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        //todo 这里输出不存在，不懂怎么回事
        System.out.println(cBuf2);
        System.out.println("---------------------");

        Charset cs2 = Charset.forName("UTF-8");
        bBuf.flip();
        CharBuffer decode = cs2.decode(bBuf);
        System.out.println(decode.toString());

    }
}
