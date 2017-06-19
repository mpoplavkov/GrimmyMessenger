package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.User;
import edu.technopolis.homework.messenger.messages.*;
import edu.technopolis.homework.messenger.store.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.*;

public class GrimmyServer {
    private static final int PORT = 10013;
    private static final int BUFFER_SIZE = 1024;

    private Protocol protocol = new SerializableProtocol();
    private Map<SocketChannel, ByteBuffer> map = new HashMap<>();

    private UserStore userStore = new UserTable();
    private MessageStore messageStore = new MessageTable();

    public void run() {
        //отсоединение от БД в конце работы
        Runtime.getRuntime().addShutdownHook(new Thread(StoreConnection::disconnect));
        try (ServerSocketChannel open = openChannel();
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

    private static ServerSocketChannel openChannel() throws IOException {
        ServerSocketChannel open = ServerSocketChannel.open();
        open.bind(new InetSocketAddress(PORT));
        open.configureBlocking(false);
        return open;
    }

    private void accept(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel accept = channel.accept(); //non-blocking
            accept.configureBlocking(false);
            //в чем разница между allocate() и allocateDirect()???
            map.put(accept, ByteBuffer.allocate(BUFFER_SIZE));
            accept.register(key.selector(), SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            ByteBuffer buffer = map.get(channel);
            int read = channel.read(buffer);
            System.out.println("Received " + buffer.position() + " bytes");
            if (read == -1) {
                close(channel);
            } else if (read > 0) {
                Message message = protocol.decode(buffer);
                Message newMessage = processMessage(message);
                protocol.encode(newMessage, buffer);
                key.interestOps(SelectionKey.OP_WRITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void write(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = map.get(channel);
        try {
            //перед тем как отправлять буфер, его надо перевести в режим чтения!!!
            buffer.flip();
            channel.write(buffer);
            buffer.compact();
            key.interestOps(SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message processMessage(Message message) {
        User user;
        String info;
        try {
            switch (message.getType()) {
                case MSG_INFO:
                    InfoMessage infoMessage = (InfoMessage) message;
                    user = userStore.getUserById(infoMessage.getUserId());
                    return new InfoResult(user.getId(), user.getLogin(), user.getAbout());
                case MSG_TEXT:
                    TextMessage textMessage = (TextMessage) message;
                    messageStore.addMessage(textMessage);

                    info = "Sent message to chat " + textMessage.getChatId() + ": " +
                            (textMessage.getText().length() > 20 ?
                                    (textMessage.getText().substring(0, 20) + "...")
                                    : textMessage.getText());
                    return new StatusMessage(true, info);
                case MSG_LOGIN:
                    LoginMessage loginMessage = (LoginMessage) message;
                    user = userStore.getUser(loginMessage.getLogin(), loginMessage.getPassword());
                    info = "id=" + user.getId() + " login=" + user.getLogin();
                    return new StatusMessage(true, info);
                case MSG_CHAT_CREATE:
                    ChatCreateMessage chatCreateMessage = (ChatCreateMessage) message;
                    //Можно это будет потом заменить на хранимую процедуру в БД, чтоб
                    //выполнялось за одну операцию
                    chatCreateMessage.getListOfInvited().add(chatCreateMessage.getSenderId());
                    long chatId = messageStore.createChat(chatCreateMessage.getName(), chatCreateMessage.getListOfInvited());
                    info = "Returned chat with chatId=" + chatId;
                    return new StatusMessage(true, info);
                case MSG_CHAT_HIST:
                    ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;
                    List<TextMessage> messages = messageStore.getMessagesFromChat(chatHistoryMessage.getSenderId(), chatHistoryMessage.getChatId());
                    return new ChatHistoryResult(messages);
                case MSG_CHAT_LIST:
                    ChatListMessage chatListMessage = (ChatListMessage) message;
                    return new ChatListResult(messageStore.getChatsByUserId(chatListMessage.getSenderId()));
                default:
                    System.out.println("Oh no! It's very bad. I should not have received this message: " + message);
                    System.exit(0);
            }
        } catch (SQLException e) {
            return new StatusMessage(false, e.getMessage());
        }
        return null;
    }

    private void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GrimmyServer().run();
    }
}
