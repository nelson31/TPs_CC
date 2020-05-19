package Tests;

import AnonProtocol.AnonSocket;
import AnonProtocol.AnonStream;
import AnonProtocol.DataInfo;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TesteRFromSocketToStreamReceiver {

    public static void main(String[] args) {

        try {
            AnonSocket socket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            AnonStream stream = new AnonStream(socket,0);
            while (true){
                DataInfo info = new DataInfo();
                byte[] data = stream.read(info);
                System.out.println("Meta-info");
                System.out.println(info.toString());
                System.out.println("Conteudo lido da stream");
                for(int i=0; i<data.length; i++)
                    System.out.print(data[i]);
            }
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println(exc.getMessage());
        }
    }
}