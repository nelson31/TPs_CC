package FinalTests;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        try {
            int size;
            Socket input;
            Thread t;
            ServerSocket ss = new ServerSocket(80, 0, InetAddress.getByName(args[0]));
            while(true){
                input = ss.accept();
                t = new Thread(new ServerWorker(input));
                t.start();
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}