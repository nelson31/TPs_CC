package FinalTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args){

        try {
            int size;
            Socket input = new Socket(InetAddress.getByName(args[0]),80);
            PrintWriter pw = new PrintWriter(input.getOutputStream());
            BufferedReader bf = new BufferedReader(new InputStreamReader(input.getInputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String message;
            message = in.readLine();
            pw.println(message);
            pw.flush();
            /* Lemos a resposta do server */
            System.out.println(bf.readLine());
            input.shutdownInput();
            input.shutdownOutput();
            input.close();
        }
        catch(
                IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
