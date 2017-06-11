package edu.technopolis.homework.messenger.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by timur on 11.05.17.
 */
public class OldGoodServer {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        try (ServerSocket serverSocket = new ServerSocket(10001)) {
            while (true) {
                Socket accept = serverSocket.accept(); //блок
                executorService.submit(() -> handle(accept));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handle(Socket accept) {
        try (InputStream inputStream = accept.getInputStream();
            OutputStream outputStream = accept.getOutputStream()) {
            int data;
            while ( -1 != (data = inputStream.read())) { //блок
                outputStream.write(doMagic(data));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int doMagic(int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
