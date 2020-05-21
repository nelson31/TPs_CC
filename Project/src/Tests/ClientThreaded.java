package Tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThreaded {

    public static void main(String args[]){

        try {
            Socket socket = new Socket(InetAddress.getByName(args[0]), 80);
            new Thread(new SocketReader(socket)).start();
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String message;
            System.out.println("À espera de mensagens: ");
            while((message = bf.readLine()) != null){
                pw.println(message);
                pw.flush();
                System.out.println("À espera de mensagens: ");
            }
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
