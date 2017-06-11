package edu.technopolis.homework.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 *
 */
public class MessengerServer {

    public static void main(String[] args) {
        try {
            //Пример серверной обработки
            Protocol protocol = new StringProtocol();
            ServerSocket serverSocket = new ServerSocket(19000);
            while (true) {
                Socket client = serverSocket.accept();
                InputStream inputStream = client.getInputStream();
                while (true) {
                    final byte[] buffer = new byte[1024 * 64];
                    int read = inputStream.read(buffer);
                    if (read > 0) {
                        try {
                            System.out.println(protocol.decode(Arrays.copyOf(buffer, read)));
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
