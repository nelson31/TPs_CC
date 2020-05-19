package Tests;

import AnonProtocol.AnonPacket;
import AnonProtocol.AnonSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class AnonTesteSender {

    public static void main(String[] args){

        try {
            AnonSocket asocket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            InetAddress destination;
            String message;
            System.out.println("Escreva uma mensagem: ");
            while((message = bf.readLine()) != null){

                destination = InetAddress.getByName(message);
                byte[] data = {12,42,23,43,23};
                AnonPacket sp = new AnonPacket(0,0,5,80,
                        InetAddress.getByName("localhost"),InetAddress.getByName(args[0]),0,data);
                asocket.send(sp,asocket.getLocalIp(),destination,6666);
                System.out.println("Escreva uma mensagem: ");
            }
        }
        catch(IOException exc){
            System.out.println("Erro ao criar socket");
        }
    }
}
