import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AnonGW {

    /**
     * Socket TCP através do qual o anonGW
     * aceita conexões provenientes de
     * clientes que se pretendem anonimizar
     * para comunicar com um dado servidor
     */
    private static ServerSocket listen;

    /**
     * Socket que permite receber dados
     * de um cliente
     */
    private static Socket input;

    /**
     * Socket que será usado para comunicar
     * com o servidor. (Apenas nesta primeira fase)
     */
    private Socket output;

    /**
     * Método a partir do qual arranca
     * o programa principal
     * @param args
     */
    public static void main(String[] args){

        try {
            /* Colocamos o servidor à escuta
            na porta 80 */
            listen = new ServerSocket(80);
            String ipadd = args[2], port = args[4];
            /* Colocamos o server socket
            permanentemente à escuta*/
            while (true) {
                System.out.println("I'm listening for new requests");
                Worker w = new Worker(listen.accept(), args[2], args[4]);
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
