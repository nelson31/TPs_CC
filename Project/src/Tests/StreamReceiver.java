package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.AnonStream;
import AnonProtocol.DataInfo;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StreamReceiver {

    public static void main(String[] args) {

        try {
            AnonStream as = new AnonStream(new AnonSocket(6666,
                    InetAddress.getByName(args[0])), Integer.parseInt(args[1]));

            while(true){
                DataInfo info = new DataInfo();
                byte[] lido = as.read(info);
                System.out.println("Meta-Info: ");
                System.out.println(info.toString());
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