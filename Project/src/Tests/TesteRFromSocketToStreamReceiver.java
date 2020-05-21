package Tests;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.DataInfo;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TesteRFromSocketToStreamReceiver {

    public static void main(String[] args) {

        try {
            AnonSocket socket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            AnonStream stream = new AnonStream(socket,0);
            /* Ativamos a extremidade de leitura */
            stream.enableInputStream();
            byte[] lido;
            while ((lido = stream.read()) != null){
                System.out.println("Conteudo lido da stream");
                for(int i=0; i<lido.length; i++)
                    System.out.print(lido[i]);
            }
            DataInfo info = stream.getTargetInfo();
            System.out.println("Tinha que enviar isto para: ");
            System.out.println(info.toString());
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println(exc.getMessage());
        }
    }
}