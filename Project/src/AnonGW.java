import AnonProtocol.AnonSocket;
import AnonProtocol.SessionGetter;
import Components.AnonAccepter;
import Components.ClientAccepter;
import Components.ForeignSessions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class AnonGW {

    /**
     * Estrutura que nos permite aceder
     * a pedidos de clientes
     */
    private static ServerSocket accepter;

    /**
     * Variável que permite comunicar
     * com outros anonGW
     */
    private static AnonSocket asocket;

    /**
     * Variável que permite atribuir id's
     * de sessão aos pedidos que vão chegando
     */
    private static SessionGetter sessionGetter;

    /**
     * Estrutura de dados que guarda a tabela
     * que associa indices locais a indices externos
     */
    private static ForeignSessions foreignSessions;

    /**
     * Estrutura de dados que guarda os
     * endereços IP de todos os peers na rede
     */
    private static List<InetAddress> peers;

    public static void main(String[] args) {

        try {
            InetAddress targetIp = InetAddress.getByName(args[1]);
            int targetPort = Integer.parseInt(args[3]);
            InetAddress localIp = InetAddress.getByName(args[5]);
            System.out.println("Reconheci ip local");
            peers = new ArrayList<>();
            for (int i = 7; i < args.length; i++)
                peers.add(InetAddress.getByName(args[i]));
            accepter = new ServerSocket(targetPort,0,targetIp);
            sessionGetter = new SessionGetter();
            foreignSessions = new ForeignSessions(sessionGetter);
            System.out.println("Vou criar anonsocket");
            asocket = new AnonSocket(6666,localIp,foreignSessions);

            System.out.println("Vou criar accepters");

            /* Criamos as threads que aceitam novos pedidos */
            ClientAccepter ca = new ClientAccepter(asocket,accepter,sessionGetter,peers);
            AnonAccepter aa = new AnonAccepter(asocket,sessionGetter,foreignSessions);

            /* Colocamos ambos os
            accepters a correr */
            new Thread(ca).start();
            new Thread(aa).start();
        }
        catch(IOException exc){
            System.out.println(exc.getLocalizedMessage());
        }
    }
}