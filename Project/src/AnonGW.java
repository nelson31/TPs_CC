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

    /**
     * Password utilizada para encriptar dados
     */
    private static String password;

    /**
     * Variáveis utilizadas para encriptar
     * e desencriptar dados
     */
    private static int Key1, Key2;

    public static void main(String[] args) {

        try {
            InetAddress targetIp = InetAddress.getByName(args[1]);
            int targetPort = Integer.parseInt(args[3]);
            InetAddress localIp = InetAddress.getByName(args[5]);
            peers = new ArrayList<>();
            for (int i = 7; i < args.length; i++) {
                System.out.println(args[i]);
                peers.add(InetAddress.getByName(args[i]));
            }
            accepter = new ServerSocket(targetPort,0,localIp);
            sessionGetter = new SessionGetter();
            foreignSessions = new ForeignSessions(sessionGetter);
            asocket = new AnonSocket(6666,localIp,foreignSessions);
            /* Atribuição de valores para a criptografia */
            password = "GT$!kT=D*k3b2U&x9?9!cFSGTfVtnm";
            Key1 = 15; Key2 = 20;

            /* Criamos as threads que aceitam novos pedidos */
            ClientAccepter ca = new ClientAccepter(asocket,accepter,sessionGetter,foreignSessions,
                    peers,targetIp,targetPort,password,Key1,Key2);
            AnonAccepter aa = new AnonAccepter(asocket,sessionGetter,foreignSessions,password,Key1,Key2);

            /* Colocamos ambos os
            accepters a correr */
            new Thread(ca).start();
            new Thread(aa).start();

            System.out.println("Tudo a correr...");
        }
        catch(IOException exc){
            System.out.println(exc.getLocalizedMessage());
        }
    }
}