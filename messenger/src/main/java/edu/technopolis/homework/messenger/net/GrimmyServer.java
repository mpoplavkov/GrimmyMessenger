package edu.technopolis.homework.messenger.net;

import edu.technopolis.homework.messenger.User;
import edu.technopolis.homework.messenger.messages.LoginMessage;
import edu.technopolis.homework.messenger.messages.Message;
import edu.technopolis.homework.messenger.messages.MsgInfoResult;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by greg on 11.06.17.
 */
public class GrimmyServer {

    private static final int SERVER_PORT = 10001;
    private static final int BUFFER_SIZE = 1024;

    private List<User> users;
    private List<User> loggedInUsers;

    public GrimmyServer(List<User> users, List<User> loggedInUsers) {
        this.users = users;
        this.loggedInUsers = loggedInUsers;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<User> getLoggedInUsers() {
        return loggedInUsers;
    }

    public static void main(String[] args) {

        List<User> users = new ArrayList<>();
        users.add(new User("greg", 1234));
        users.add(new User("miha", 1234));
        GrimmyServer server = new GrimmyServer(users, new ArrayList<User>());

        HashMap<SocketChannel, ByteBuffer> map = new HashMap<>();

        try (ServerSocketChannel open = openAndBind()) {
            open.configureBlocking(false);
            while (true) {
                SocketChannel accept = open.accept(); //не блокируется

                if (accept != null) {
                    accept.configureBlocking(false);
                    map.put(accept, ByteBuffer.allocateDirect(BUFFER_SIZE));
                }

                map.keySet().removeIf(sc -> !sc.isOpen());
                map.forEach((sc,byteBuffer) -> {
                    try {
                        byte[] arr = new byte[BUFFER_SIZE];
                        int idx = 0;
                        int read = sc.read(byteBuffer);
                        arr[idx++] = (byte) read;
                        //TODO: не противоречит ли это non-blocking??
                        while(read != -1) {
                            read = sc.read(byteBuffer);
                            arr[idx++] = (byte) read;
                        }
                            //теперь буфер готов к чтению из него
                            byteBuffer.flip();

                            try {
                                //Должны восстаность сообщение
                                Message msg = new StringProtocol().decode(arr);

                                //Здесь выполняем всю логику
                                performWork(msg, server);

                                //TODO: перенести формирование ответа клиенту в другое метсо
                                Message msgResp = new MsgInfoResult("bla bla");
                                byte[] response = new StringProtocol().encode(msgResp);
                                byteBuffer.put(response);

                                sc.write(byteBuffer);

                                //чтобы буфер снова был готов к записи
                                //byteBuffer.compact();
                                byteBuffer.clear();

                            } catch (Exception e) {
                                //подробнее
                                e.printStackTrace();
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
        open.bind(new InetSocketAddress(SERVER_PORT));
        return open;
    }

    private static void close(SocketChannel sc) {
        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static int performWork(int data) {
//        return Character.isLetter(data) ? data ^ ' ' : data;
//    }

    private static void performWork(Message msg, GrimmyServer server) throws ProtocolException, CommandException {
        switch(msg.getType()) {
            case MSG_LOGIN:
                Session session = new Session(new User(((LoginMessage) msg).getLogin(),
                                                ((LoginMessage) msg).getPassword()));
                msg.execute(session, server);

                //формируем ответ клиенту
                break;

            case MSG_TEXT:

                break;

            default:
                throw new ProtocolException("Invalid type: " + msg.getType());
        }

//        for(int i = 0; i < buffer.limit(); ++i) {
//            buffer.put(i, (byte) performWork(buffer.get(i)));
//        }
    }
}
