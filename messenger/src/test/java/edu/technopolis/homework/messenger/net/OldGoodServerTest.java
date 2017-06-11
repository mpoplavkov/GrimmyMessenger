package edu.technopolis.homework.messenger.net;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by timur on 11.05.17.
 */
public class OldGoodServerTest {
    @Test
    public void test() throws IOException, InterruptedException {
        Socket[] sockets = new Socket[3000];
        for (int i = 0; i < sockets.length; i++) {
            sockets[i] = new Socket("localhost", 10001);
            System.out.println(i + " " + sockets[i]);
        }
        Thread.sleep(100000L);
    }

}