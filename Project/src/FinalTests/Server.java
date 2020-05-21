package FinalTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        try {
            int size;
            Socket input;
            ServerSocket ss = new ServerSocket(80, 0, InetAddress.getByName(args[0]));
            while(true){
                input = ss.accept();
                BufferedReader bf = new BufferedReader(new InputStreamReader(input.getInputStream()));
                PrintWriter pw = new PrintWriter(input.getOutputStream());
                String message;
                while((message = bf.readLine()) != null){
                    size = message.length();
                    pw.println("Tamanho da string: " + size);
                    pw.flush();
                }
                input.shutdownInput();
                input.shutdownOutput();
                input.close();
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}