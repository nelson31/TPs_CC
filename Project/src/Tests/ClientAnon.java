package Tests;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class ClientAnon {

    public static void main(String[] args){

        try {
            InetAddress origem = InetAddress.getByName(args[0]);
            InetAddress destino = InetAddress.getByName(args[1]);
            AnonStream stream = new AnonStream(new AnonSocket(6666, InetAddress.getByName(args[0])),0);
            String message;
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("À espera de mensagens: ");
            while((message = bf.readLine()) != null){
                stream.send(message.getBytes(),new IntegerEncapsuler(0),origem,destino,
                        InetAddress.getByName("localhost"),origem,6666,80);
                System.out.println("À espera de mensagens: ");
            }
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
