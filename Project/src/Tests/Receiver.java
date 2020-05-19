package Tests;

import SecureProtocol.SecurePacket;
import SecureProtocol.SecureSocket;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Receiver {

    public static void main(String[] args){

        try {
            SecureSocket asocket = new SecureSocket(6666, InetAddress.getByName(args[0]));
            while(true) {
                System.out.println("À espera do pacote: ");
                SecurePacket sp = asocket.receive();
                System.out.println("Packet received: ");
                System.out.println(sp.toString());
            }
        }
        catch(UnknownHostException | SocketException exc){
            System.out.println("Erro ao criar socket");
        }
    }
}
