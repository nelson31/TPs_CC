package Tests;

import AnonProtocol.AnonSocket;
import Components.OwnWorker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class OwnWorkerTest {

    public static void main(String[] args){

        try {
            ServerSocket accept = new ServerSocket(80, 0, InetAddress.getByName(args[0]));
            AnonSocket asocket = new AnonSocket(6666, InetAddress.getByName(args[0]));
            InetAddress nextIp = InetAddress.getByName(args[1]);
            System.out.println("À espera de conexões");
            /* Colocamos a correr um programa que trata da sessão 0,
            aceitando dados de um socket TCP e enviando para um peer e
            recebendo no sentido contrário */
            OwnWorker worker = new OwnWorker(0, accept.accept(),asocket,nextIp,
                    InetAddress.getByName("localhost"),80);

            /* Colocamos o worker a correr */
            new Thread(worker).start();
        }
        catch(IOException exc){
            System.out.println(exc.getMessage());
        }
    }
}
