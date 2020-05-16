import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class AnonGW {

    /**
     * Socket TCP através do qual o anonGW
     * aceita conexões provenientes de
     * clientes que se pretendem anonimizar
     * para comunicar com um dado servidor
     */
    private static ServerSocket listen;


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
            String ipadd = args[1], port = args[3];
            System.out.println("I'm protecting you to access to " + ipadd + ", port " + port);
            /* Colocamos o server socket
            permanentemente à escuta*/
            while (true) {
                System.out.println("I'm listening for new requests");
                Worker w = new Worker(listen.accept(), ipadd, port);
                Thread t = new Thread(w);
                t.start();
                System.out.println("New request");
            }
        }
        catch(IOException exc){
            System.out.println("IOError");
        }
    }
}
