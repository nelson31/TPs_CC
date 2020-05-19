package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.AnonStream;
import AnonProtocol.IntegerEncapsuler;
import Components.ReaderFromSocketToStream;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class TesteRFromSocketToStreamSender {

    public static void main(String[] args) {

        try {
            InetAddress destino = InetAddress.getByName(args[1]);
            ServerSocket accept = new ServerSocket(80,0,InetAddress.getByName(args[0]));
            AnonStream stream = new AnonStream(new AnonSocket(6666, InetAddress.getByName(args[0])),0);
            ReaderFromSocketToStream reader = new ReaderFromSocketToStream(stream,
                    accept.accept(),0,new IntegerEncapsuler(0),destino,
                    6666,InetAddress.getByName("localhost"),80);
            /* Colocamos a thread a correr */
            new Thread(reader).start();
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}