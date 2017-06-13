package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.User;
import edu.technopolis.homework.messenger.messages.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class GrimmyClient {
    private static final String HOST = "localhost";
    private static final int PORT = 10013;
    private static final int BUFFER_SIZE = 1024;

    Protocol protocol = new SerializableProtocol();
    private BlockingQueue<Message> queue = new ArrayBlockingQueue<>(2);
    private User user;

    private void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Selector selector = Selector.open()) {
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(HOST, PORT));
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                selectionKeys.removeIf(key -> {
                    if (!key.isValid()) {
                        return true;
                    }
                    if (key.isConnectable()) {
                        try {
                            if (socketChannel.finishConnect()) {
                                System.out.println("Client: connected");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(0);
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
                        Message message = null;
                        try {
                            message = protocol.decode(byteBuffer);
                        } catch (ProtocolException e) {
                            //здесь надо обработать случаи, когда message передается не за один раз
                            e.printStackTrace();
                        }
                        if (message != null) {
                            onMessage(message);
                        }
                    } else if (key.isWritable()) {
                        Message message = queue.poll();
                        if (message != null) {
                            System.out.println("Client: sending " + message);
                            try {
                                protocol.encode(message, byteBuffer);
                                //перед тем как отправлять буфер, его надо перевести в режим чтения!!!
                                byteBuffer.flip();
                                socketChannel.write(byteBuffer);
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

    private void onMessage(Message message) {
        System.out.println("Received " + message.getType());
        switch (message.getType()) {
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage)message;
                if (user == null && statusMessage.getStatus()) {
                    String[] tokens = statusMessage.getInfo().split(" ");
                    Map<String, String> map = Arrays.stream(tokens).collect(Collectors.toMap(
                            s -> s.substring(0, s.indexOf('=') == -1 ? 0 : s.indexOf('=')),
                            s -> s.substring((s.indexOf('=') + 1) == s.length() ? 0 : (s.indexOf('=') + 1), s.length())));
                    Long id = parseLong(map.get("id"));
                    String login = map.get("login");
                    user = new User(id, login);
                    System.out.println("Login successful");
                }
                System.out.println("---status   = " + statusMessage.getStatus());
                System.out.println("---info     = " + statusMessage.getInfo());
                break;
            case MSG_INFO_RESULT:
                InfoResult infoResult = (InfoResult) message;
                System.out.println("---id       = " + infoResult.getUserId());
                System.out.println("---login    = " + infoResult.getLogin());
                System.out.println("---about    = " + infoResult.getAbout());
                break;
            case MSG_CHAT_HIST_RESULT:
                ChatHistoryResult chatHistoryResult = (ChatHistoryResult)message;
                System.out.println("---msgList  = ");
                for (Message mes : chatHistoryResult.getList()) {
                    System.out.println("\t\t\t\t" + mes);
                }
                break;
            case MSG_CHAT_LIST_RESULT:
                ChatListResult chatListResult = (ChatListResult)message;
                System.out.println("---chatList = " + chatListResult.getChats());
                break;
            default:
                System.out.println("Oh no! It's very bad. I should not have received this message: " + message);
                System.exit(0);
        }
    }

    /**
     * @return null if message process on client or invalid input
     */
    private Message processInput(String line) {
        String[] tokens = line.split(" ");
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                if (tokens.length == 3) {
                    if (user != null) {
                        System.out.println("You're already logged in");
                        return null;
                    } else {
                        return new LoginMessage(tokens[1], tokens[2]);
                    }
                }
                System.out.println("Incorrect operands.");
                break;
            case "/help":
                System.out.println("" +
                        "/login <login> <password>      - вход в систему.\n" +
                        "/text <chatId> <message>       - отправить сообщение message в указанный чат.\n" +
                        "/info [userId]                 - получить информацию о себе (если id не указан) или о пользователе с id = userId.\n" +
                        "/chat_list                     - получить список чатов пользователя.\n" +
                        "/chat_create <userId list>     - создать чат или вернуть существующий, если указан один userId. <userId list> - <userId,userId,userId...>\n" +
                        "/chat_history <chat_id>        - получить список сообщений из указанного чата.");
                return null;
            case "/text":
                if (user != null) {
                    if (tokens.length > 2) {
                        long chatId = parseLong(tokens[1]);
                        if (chatId > 0) {
                            return new TextMessage(user.getId(), chatId, line.substring(tokens[0].length() + tokens[1].length() + 2));
                        }
                    }
                    System.out.println("Incorrect operands.");
                } else {
                    System.out.println("You're not logged in");
                }
                break;
            case "/info":
                if (user != null) {
                    if (tokens.length == 1) {
                        return new InfoMessage(user.getId());
                    } else if (tokens.length == 2) {
                        long userId = parseLong(tokens[1]);
                        if (userId > 0) {
                            return new InfoMessage(user.getId(), userId);
                        }
                    }
                    System.out.println("Incorrect operands.");
                } else {
                    System.out.println("You're not logged in");
                }
                break;
            case "/chat_list":
                if (user != null) {
                    if (tokens.length == 1) {
                        return new ChatListMessage(user.getId());
                    }
                    System.out.println("Incorrect operands.");
                } else {
                    System.out.println("You're not logged in");
                }
                break;
            case "/chat_create":
                if (user != null) {
                    if (tokens.length == 2) {
                        Set<Long> users = parseLongList(tokens[1]);
                        if (users != null) {
                            return new ChatCreateMessage(user.getId(), users.toString(), users);
                        }
                    }
                    System.out.println("Incorrect operands.");
                } else {
                    System.out.println("You're not logged in");
                }
                break;
            case "/chat_history":
                if (user != null) {
                    if (tokens.length == 2) {
                        Long chatId = parseLong(tokens[1]);
                        if (chatId > 0) {
                            return new ChatHistoryMessage(user.getId(), chatId);
                        }
                    }
                    System.out.println("Incorrect operands.");
                } else {
                    System.out.println("You're not logged in");
                }
                break;
            default:
                System.out.println("Incorrect command.");
        }

        System.out.println("Try \'/help\' for more information");
        return null;
    }

    private static long parseLong(String token) {
        try {
            return new Long(token);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static Set<Long> parseLongList(String token) {
        String[] tokens = token.split(",");
        Set<Long> set = new HashSet<>();
        for (String s : tokens) {
            long l = parseLong(s);
            if (l > 0) {
                set.add(l);
            } else {
                return null;
            }
        }
        return set;
    }

    public static void main(String[] args) {
        new GrimmyClient().run();
    }
}
