package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.AnonStream;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StreamReceiver {

    public static void main(String[] args) {

        try {
            AnonStream as = new AnonStream(new AnonSocket(6666,
                    InetAddress.getByName(args[0])), Integer.parseInt(args[1]));

            while(true){
                byte[] lido = as.read();
                System.out.println("Conteudo lido da stream");
                for(int i=0; i<lido.length; i++)
                    System.out.print(lido[i]);
                System.out.println();
            }
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println(exc.getMessage());
        }
    }
}