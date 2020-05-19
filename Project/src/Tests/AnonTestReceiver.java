package Tests;

import AnonProtocol.AnonPacket;
import AnonProtocol.AnonSocket;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class AnonTestReceiver {

    public static void main(String[] args){

        try {
            AnonSocket asocket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            while(true) {
                System.out.println("À espera do pacote: ");
                AnonPacket sp = asocket.receive(0);
                System.out.println("Packet received: ");
                System.out.println(sp.toString());
            }
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println("Erro ao criar socket");
        }
    }
}
