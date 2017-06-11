package edu.technopolis.homework.messenger.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by timur on 11.05.17.
 */
public class SkolkovoServer {
    private static Map<SocketChannel, ByteBuffer> map = new HashMap<>();

    public static void main(String[] args) throws IOException {
        try(ServerSocketChannel open = openChannel();
            Selector selector = Selector.open()) {
            open.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select(); //blocking
                Set<SelectionKey> keys = selector.selectedKeys();
                keys.removeIf(key -> {
                    if (!key.isValid()) {
                        return true;
                    }
                    if (key.isAcceptable()) {
                        accept(key);
                    } else if (key.isReadable()) {
                        read(key);
                    } else if (key.isWritable()) {
                        write(key);
                    }
                    return true;
                });
                map.keySet().removeIf(sc -> !sc.isOpen());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = map.get(channel);
        try {
            channel.write(buffer);
            buffer.compact();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer buffer = map.get(channel);
            int read = channel.read(buffer);
            if (read == -1) {
                close(channel);
            } else if (read > 0) {
                buffer.flip();
                doMagic(buffer);
                key.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        for (int i = data.position(); i < data.limit(); i++) {
            data.put(i, (byte) doMagic(data.get(i)));
        }
    }

    private static void accept(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = channel.accept(); //non-blocking
            accept.configureBlocking(false);
            map.put(accept, ByteBuffer.allocateDirect(1024));
            accept.register(key.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ServerSocketChannel openChannel() throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(10013));
        open.configureBlocking(false);
        return open;
    }
}
