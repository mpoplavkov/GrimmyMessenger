package edu.technopolis.homework.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by timur on 11.05.17.
 */
public class NonBlockingServer {
    public static void main(String[] args) {
        HashMap<SocketChannel, ByteBuffer> map = new HashMap<>();
        try (ServerSocketChannel open = openAndBind()) {
            open.configureBlocking(false);
            while (true) {
                SocketChannel accept = open.accept(); //не блокируется
                if (accept != null) {
                    accept.configureBlocking(false);
                    map.put(accept, ByteBuffer.allocateDirect(1024));
                }
                map.keySet().removeIf(sc -> !sc.isOpen());
                map.forEach((sc,byteBuffer) -> {
                    try {
                        int read = sc.read(byteBuffer);
                        if (read == -1) {
                            close(sc);
                        } else if (read > 0) {
                            byteBuffer.flip();
                            doMagic(byteBuffer);
                            sc.write(byteBuffer);
                            byteBuffer.compact();
                        }
                    } catch (IOException e) {
                        close(sc);
                        e.printStackTrace();
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static ServerSocketChannel openAndBind() throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(10001));
        return open;
    }

    private static void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static int doMagic(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }

    private static void doMagic(ByteBuffer data) {
        for (int i = 0; i < data.limit(); i++) {
             data.put(i, (byte) doMagic(data.get(i)));
        }
    }
}
