package Tests;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String args[]){

        try {
            Socket socket = new Socket(InetAddress.getByName(args[0]), 80);
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
