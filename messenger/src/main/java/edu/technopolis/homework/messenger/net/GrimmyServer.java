package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class GrimmyServer {
    private static final int PORT = 10013;
    private static final int BUFFER_SIZE = 1024;
    private Protocol protocol = new SerializableProtocol();
    private Map<SocketChannel, ByteBuffer> map = new HashMap<>();

    public void run() {
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

    private void write(SelectionKey key) {
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

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer buffer = map.get(channel);
            int read = channel.read(buffer);
            if (read == -1) {
                close(channel);
            } else if (read > 0) {
                buffer.flip();
                //Не знаю, как еще получить готовый массив байт из буффера
                //Как варик, можно каждый раз создавать новый буффер методом ByteBuffer.wrap(), как реализовано ниже.
                //В таком случае в буффере не будет "пустых" байт
                byte[] bytes = new byte[buffer.limit() - buffer.position()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = buffer.get();
                }
                Message message = protocol.decode(bytes);
                Message newMessage = processMessage(message);
                map.put(channel, ByteBuffer.wrap(protocol.encode(newMessage)));
                key.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message processMessage(Message message) {
        return message;
    }

    private void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = channel.accept(); //non-blocking
            accept.configureBlocking(false);
            //в чем разница между allocate() и allocateDirect()???
            map.put(accept, ByteBuffer.allocateDirect(BUFFER_SIZE));
            accept.register(key.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ServerSocketChannel openChannel() throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(PORT));
        open.configureBlocking(false);
        return open;
    }

    public static void main(String[] args) {
        new GrimmyServer().run();
    }
}
