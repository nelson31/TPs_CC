package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class StreamSender {

    public static void main(String[] args) {

        try {
            InetAddress origem = InetAddress.getByName(args[0]);
            InetAddress destino;
            AnonStream as = new AnonStream(new AnonSocket(6666, origem),0);
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            String message;
            byte[] data = {23,43,42,5,2,34,43};
            System.out.println("Inserir destino: ");
            while((message = bf.readLine()) != null){
                destino = InetAddress.getByName(message);
                as.send(data,new IntegerEncapsuler(0),origem,destino,origem,6666,80);
                System.out.println("Inserir destino: ");
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}