package com.yzj.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 1.缓冲区（buffer):在 java NIO 中负责存取数据
 * 缓冲区在JAVA中是以数组的形式实现的，而数组就是用来存储不同数据类型的数据结构
 * 意味着 根据数据类型的不同 就会存在不同的缓冲区(boolean除外)
 * 例如：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * 上述的缓冲区的管理方式几乎一直，都是通过 allocate()来获得缓冲区，唯一的不同就是数据类型的不同
 * 最常用的就是 ByteBuffer
 * <p>
 * 2 缓冲区数据的两个核心方法
 * put():存入数据到缓冲区中
 * get():获取缓冲区中的数据
 * <p>
 * 3 缓冲区中的4个核心属性
 * capacity：容量，表示缓冲区中最大的存储数据的容量，一但声明不能改变(其实就是用来声明数组的大小的，而数组一初始化，容量就是固定的的了)
 * limit :界限，表示缓冲区中可以操作数据的大小。（limit 后面数据是不能读写的)
 * position：位置，表示缓冲区中正在操作数据的位置。
 * 0<=mark<=position<=limit<=capacity
 * mark: 标记，表示记录当前position的位置可以通过 reset()恢复到刚才 mark 的位置
 * <p>
 * flip()方法可以切换到读数据的模式，默认存
 * <p>
 * 5 直接缓冲区跟非直接缓冲区
 * 非直接缓冲区：通过allocate()方法分配的缓冲区，将缓冲区建立在 jvm 的内存中
 * 直接缓冲区：通过allocateDirect()方法分配直接缓冲区，将缓冲区建立在操作系统的物理内存中，可以提高效率
 */
@SuppressWarnings("Duplicates")
public class TestBuffer {
    @Test
    public void test1() {
        String str = "abcde";

        //1 分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //2 利用put方法存入数据到缓冲中去
        buf.put(str.getBytes());
        System.out.println("------put---------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //3 切换成读的模式
        buf.flip();
        System.out.println("-----------------flip-------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //4 利用 get()读取缓冲区的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println("-----get------------");
        System.out.println(new String(dst, 0, dst.length));
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //5rewind() 可重复读
        buf.rewind();
        System.out.println("------rewind-------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //6 clear()清空缓冲区，但是缓冲区中的数据依然存在，但是处于被遗忘的状态；
        buf.clear();
        System.out.println("------clear-------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println((char) buf.get(1));
    }

    @Test
    public void test2() {
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());

        buf.flip();
        byte[] dst = new byte[buf.limit()];
        buf.get(dst, 0, 2);
        System.out.println("-------get--------------");
        System.out.println(new String(dst, 0, 2));
        System.out.println(buf.position());

        //mark标记一下
        buf.mark();
        buf.get(dst, 2, 2);
        System.out.println("-------get--------------");

        System.out.println(new String(dst, 2, 2));
        System.out.println(buf.position());

        System.out.println("-------reset--------------");

        //reset()恢复到 mark的位置
        buf.reset();
        System.out.println(buf.position());

        System.out.println("-------remaining--------------");
        //buf.hasRemaining() 判断缓冲区中是否还有剩余的数据
        if (buf.hasRemaining()) {
            System.out.println(buf.remaining());
        }
    }


    @Test
    public void test3() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println(byteBuffer.isDirect());
    }


}
