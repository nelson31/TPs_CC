import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class AnonGW {

    /**
     * Socket TCP através do qual o anonGW
     * aceita conexões provenientes de
     * clientes que se pretendem anonimizar
     * para comunicar com um dado servidor
     */
    private static ServerSocket listen;

    /**
     * Socket para comunicar com os peers;
     */
    private static AnonSocket asocket;

    /**
     * Variável que guarda o endereço IP do
     * target server que o anonGW está a proteger
     */
    private static String protectedTarget;

    /**
     * Variável que guarda a porta do
     * target server
     */
    private static int portTarget;

    /**
     * Variável que guarda od ids de sessão
     * cuja sessão é de origem externa
     */
    private static ForeignSessions foreignSessions;

    /**
     * Variável que permite obter ids de sessões
     * mediante chegadas de novos pedidos
     */
    private static SessionGetter idSessionGetter;

    private static List<String> peers;

    /**
     * Variável que guarda o endereço
     * IP do anonGW em questão
     */
    private static String myIP;


    /**
     * Método a partir do qual arranca
     * o programa principal
     * @param args
     */
    public static void main(String[] args){

        try {
            /* Colocamos o servidor à escuta
            na porta 80 */
            listen = new ServerSocket(Integer.parseInt(args[3]));
            protectedTarget = args[1]; portTarget = Integer.parseInt(args[3]);
            idSessionGetter = new SessionGetter();
            foreignSessions = new ForeignSessions();
            /* Colocamos um anonSocket à escuta na porta 6666 */
            asocket = new AnonSocket(6666,myIP,foreignSessions,idSessionGetter);
            peers = new ArrayList<>();
            myIP = args[6];
            /* Colocamos os peers na lista */
            for(int i=7; i<args.length; i++)
                peers.add(args[i]);

            /* Colocamos a correr a thread que aceita e trata de pedidos de clientes */
            new Thread(new ClientAccepter(listen, asocket, idSessionGetter,
                    protectedTarget, portTarget, peers)).start();

            /* Colocamos a correr a thread que aceita e trata de pedidos de peers */
            new Thread(new PeerAccepter(asocket,foreignSessions)).start();
        }
        catch(IOException exc){
            System.out.println("IOError");
        }
    }
}
