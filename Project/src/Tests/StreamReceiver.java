package Tests;

import AnonProtocol.AnonSocket;
import AnonStreamProtocol.AnonStream;
import AnonProtocol.DataInfo;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class StreamReceiver {

    public static void main(String[] args) {

        try {
            AnonStream as = new AnonStream(new AnonSocket(6666,
                    InetAddress.getByName(args[0])), Integer.parseInt(args[1]));
            /* Ativamos a extremidade de
            leitura da stream */
            as.enableInputStream();

            DataInfo info = new DataInfo();
            byte[] lido;
            while((lido = as.read()) != null){
                System.out.println("Conteudo lido da stream");
                for(int i=0; i<lido.length; i++)
                    System.out.print(lido[i]);
                System.out.println();
                if(!info.isComplete())
                    info = as.getTargetInfo();
            }
            System.out.println("Destino dos dados: ");
            System.out.println(info.toString());
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println(exc.getMessage());
        }
    }
}