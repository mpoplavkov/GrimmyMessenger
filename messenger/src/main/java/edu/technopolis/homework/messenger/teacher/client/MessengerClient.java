package edu.technopolis.homework.messenger.teacher.client;

import edu.technopolis.homework.messenger.messages.*;
import edu.technopolis.homework.messenger.net.Protocol;
import edu.technopolis.homework.messenger.net.ProtocolException;
import edu.technopolis.homework.messenger.net.SerializableProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Long.parseLong;


/**
 *
 */
public class MessengerClient {

    /**
     * Протокол, хост и порт инициализируются из конфига
     */
    private Protocol protocol;
    private int port;
    private String host;

    /**
     * Залогинился или нет
     */
    private boolean isLogin = true;

    /**
     * С каждым сокетом связано 2 канала in/out
     */
    private InputStream in;
    private OutputStream out;

    public static void main(String[] args) throws Exception {

        MessengerClient client = new MessengerClient();
        client.setHost("localhost");
        client.setPort(19000);
        client.setProtocol(new SerializableProtocol());

        try {
            client.initSocket();

            // Цикл чтения с консоли
            Scanner scanner = new Scanner(System.in);
            System.out.println("$");
            while (true) {
                String input = scanner.nextLine();
                if ("q".equals(input)) {
                    return;
                }
                try {
                    client.processInput(input);
                } catch (ProtocolException | IOException e) {
                    System.err.println("Failed to process user input " + e);
                }
            }
        } catch (Exception e) {
            System.err.println("Application failed. " + e);
        } finally {
            if (client != null) {
                // TODO
//                client.close();
            }
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void initSocket() throws IOException {
        Socket socket = new Socket(host, port);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        // Тред "слушает" сокет на наличие входящих сообщений от сервера
        Thread socketListenerThread = new Thread(() -> {
            final byte[] buf = new byte[1024 * 64];
            System.out.println("Starting listener thread...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Здесь поток блокируется на ожидании данных
                    int read = in.read(buf);
                    if (read > 0) {

                        // По сети передается поток байт, его нужно раскодировать с помощью протокола
                        Message msg = protocol.decode(Arrays.copyOf(buf, read));
                        onMessage(msg);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process connection: " + e);
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });
        socketListenerThread.start();
    }

    /**
     * Реагируем на входящее сообщение
     */
    public void onMessage(Message msg) {

        /*
        // FIXME  WHAT THE HELL GOING ON????????
        switch (msg.getType()) {
            case MSG_STATUS:
                StatusMessage statusMessage = (StatusMessage) msg;
                if (statusMessage.getStatus()) {
                    isLogin = true;
                    System.out.println(statusMessage.getInfo());
                } else System.err.println(statusMessage.getInfo());
                break;
            case MSG_CHAT_HIST_RESULT:
                ChatHistoryResult chatHistoryResult = (ChatHistoryResult) msg;
                for (int i = 0; i < chatHistoryResult.getList().size(); i++) {
                    System.out.println(chatHistoryResult.getList().get(i).getSenderId() + ": " +
                            chatHistoryResult.getList().get(i).getText());
                }
                break;
            default:
                System.err.println("Message received:  " + msg);
                break;
        }
        */

    }

    /**
     * Обрабатывает входящую строку, полученную с консоли
     * Формат строки можно посмотреть в вики проекта
     */
    public void processInput(String line) throws IOException, ProtocolException {
        /*
        String[] tokens = line.split(" ");
        System.out.println("Tokens: " + Arrays.toString(tokens));
        String cmdType = tokens[0];
        switch (cmdType) {
            case "/login":
                LoginMessage loginMessage = new LoginMessage();
                loginMessage.setType(Type.MSG_LOGIN);
                loginMessage.setLogin(tokens[1]);
                loginMessage.setPassword(tokens[2]);
                send(loginMessage);
                break;
            case "/help":
                // TODO: реализация
                break;
            case "/text":
                if (isLogin) {
                    if (tokens.length > 2) {
                        TextMessage sendMessage = new TextMessage();
                        sendMessage.setType(Type.MSG_TEXT);
                        sendMessage.setChatId(parseLong(tokens[1]));
                        sendMessage.setText(tokens[2]);
                        send(sendMessage);
                    } else System.err.println("Notification: Command = <chatId> <text>");
                } else System.err.println("Notification: Please login");
                break;
            case "/info":
                if (isLogin) {
                    InfoMessage infoMessage = new InfoMessage();
                    infoMessage.setType(Type.MSG_INFO);
                    // если аргумент<id> есть, то об <id> пользователе
                    // если нет аргумента<id>, то о себе
                    if (tokens.length > 1)
                        infoMessage.setUserId(parseLong(tokens[1]));
                    send(infoMessage);
                } else System.err.println("Notification: Please login");
                break;
            case "/chat_list":
                if (isLogin) {
                    ChatListMessage chatListMessage = new ChatListMessage();
                    chatListMessage.setType(Type.MSG_CHAT_LIST);
                    send(chatListMessage);
                } else System.err.println("Notification: Please login");
                break;
            case "/chat_create":
                if (isLogin && tokens.length > 1) {
                    ChatCreateMessage chatCreateMessage = new ChatCreateMessage();
                    chatCreateMessage.setType(Type.MSG_CHAT_CREATE);
                    Set<Long> list = new HashSet<>();
                    for (int i = 1; i < tokens.length; i++) {
                        list.add(parseLong(tokens[i]));
                    }
                    chatCreateMessage.setListOfInvited(list);
                    send(chatCreateMessage);
                } else System.err.println("Notification: Please login");
                break;
            case "/chat_history":
                if (isLogin && tokens.length > 1) {
                    ChatHistoryMessage chatHistoryMessage = new ChatHistoryMessage();
                    chatHistoryMessage.setType(Type.MSG_CHAT_HIST);
                    chatHistoryMessage.setChatId(parseLong(tokens[1]));
                    send(chatHistoryMessage);
                } else System.err.println("Notification: Please login");
                break;
            default:
                System.err.println("Invalid input: " + line);
        }
        */
    }

    /**
     * Отправка сообщения в сокет клиент -> сервер
     */
    public void send(Message msg) throws IOException, ProtocolException {
        System.out.println(msg);
        out.write(protocol.encode(msg));
        out.flush(); // принудительно проталкиваем буфер с данными
    }
}