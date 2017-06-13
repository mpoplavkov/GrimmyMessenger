package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.TextMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class GrimmyClient {
    public static final String HOST = "localhost";
    public static final int PORT = 10013;
    public static final int BUFFER_SIZE = 1024;
    Protocol protocol = new SerializableProtocol();
    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(2);

    private void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            ByteBuffer byteBuffer;
            byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.removeIf(key -> {
                    if (!key.isValid()) {
                        return true;
                    }
                    if (key.isConnectable()) {
                        System.out.println("Client: connected");
                        try {
                            socketChannel.finishConnect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // поток для чтения входной инфы
                        new Thread(() -> {
                            Scanner scanner = new Scanner(System.in);
                            while (true) {
                                String line = scanner.nextLine(); // blocking
                                Message message = processInput(line);
                                if (message != null) {
                                    try {
                                        queue.put(message);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    key.interestOps(SelectionKey.OP_WRITE);
                                    selector.wakeup();
                                }
                            }
                        }).start();
                    } else if (key.isReadable()) {
                        byteBuffer.clear();
                        try {
                            socketChannel.read(byteBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byteBuffer.flip();
                        Message message = null;
                        try {
                            message = protocol.decode(byteBuffer.array());
                        } catch (ProtocolException e) {
                            //здесь надо обработать случаи, когда message передается не за один раз
                            e.printStackTrace();
                        }
                        if (message != null) {
                            processMessage(message);
                        }
                    } else if (key.isWritable()) {
                        Message message = queue.poll();
                        if (message != null) {
                            System.out.println("Client: sending " + message);
                            try {
                                socketChannel.write(ByteBuffer.wrap(protocol.encode(message)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                    return true;
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(Message message) {
        System.out.println("Client: receives " + message);
    }

    /**
     * @return null if message process on client
     */
    private static Message processInput(String line) {
        return new TextMessage(10, 100, 1000, "HOlA");
    }

    public static void main(String[] args) {
        new GrimmyClient().run();
    }
}
