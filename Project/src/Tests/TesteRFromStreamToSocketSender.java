package Tests;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import Components.ReaderFromStreamToSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TesteRFromStreamToSocketSender {

    public static void main(String[] args) {

        try {
            AnonSocket asocket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            Socket sock = new Socket(InetAddress.getByName(args[1]),80);
            ReaderFromStreamToSocket reader = new ReaderFromStreamToSocket(new AnonStream(asocket,0),sock,0);
            /* Colocamos a thread à escuta de dados
            provenientes da stream */
            new Thread(reader).start();
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}